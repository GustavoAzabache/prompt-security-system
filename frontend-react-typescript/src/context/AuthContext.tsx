import { createContext, useCallback, useMemo, useState, type ReactNode } from 'react';
import { STORAGE_KEYS } from '@/constants/roles';
import { employeeService } from '@/services/employeeService';
import { externalUserService } from '@/services/externalUserService';
import type {
  JwtPayload,
  LoginRequest,
  RegisterEmployeeRequest,
  RegisterExternalRequest,
  UserType,
} from '@/types/auth.types';
import { decodeJwt, isDirector, isEmployee } from '@/utils/jwt';

interface AuthState {
  accessToken: string | null;
  userType: UserType | null;
  payload: JwtPayload | null;
  isAuthenticated: boolean;
  isEmployee: boolean;
  isDirector: boolean;
}

interface AuthContextValue extends AuthState {
  login: (userType: UserType, dto: LoginRequest) => Promise<void>;
  registerEmployee: (dto: RegisterEmployeeRequest) => Promise<void>;
  registerExternal: (dto: RegisterExternalRequest) => Promise<void>;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextValue | null>(null);

function initState(): AuthState {
  const accessToken = localStorage.getItem(STORAGE_KEYS.accessToken);
  const userType = localStorage.getItem(STORAGE_KEYS.userType) as UserType | null;
  const payload = decodeJwt(accessToken);
  return {
    accessToken,
    userType,
    payload,
    isAuthenticated: !!accessToken,
    isEmployee: isEmployee(payload),
    isDirector: isDirector(payload),
  };
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<AuthState>(initState);

  const persist = useCallback((accessToken: string, refreshToken: string, userType: UserType) => {
    localStorage.setItem(STORAGE_KEYS.accessToken, accessToken);
    localStorage.setItem(STORAGE_KEYS.refreshToken, refreshToken);
    localStorage.setItem(STORAGE_KEYS.userType, userType);
    const payload = decodeJwt(accessToken);
    setState({
      accessToken,
      userType,
      payload,
      isAuthenticated: true,
      isEmployee: isEmployee(payload),
      isDirector: isDirector(payload),
    });
  }, []);

  const login = useCallback(
    async (userType: UserType, dto: LoginRequest) => {
      const data =
        userType === 'external'
          ? await externalUserService.login(dto)
          : await employeeService.login(dto);
      persist(data.accessToken, data.refreshToken, userType);
    },
    [persist],
  );

  const registerEmployee = useCallback(
    async (dto: RegisterEmployeeRequest) => {
      const data = await employeeService.register(dto);
      persist(data.accessToken, data.refreshToken, 'employee');
    },
    [persist],
  );

  const registerExternal = useCallback(
    async (dto: RegisterExternalRequest) => {
      const data = await externalUserService.register(dto);
      persist(data.accessToken, data.refreshToken, 'external');
    },
    [persist],
  );

  const logout = useCallback(() => {
    localStorage.removeItem(STORAGE_KEYS.accessToken);
    localStorage.removeItem(STORAGE_KEYS.refreshToken);
    localStorage.removeItem(STORAGE_KEYS.userType);
    setState({
      accessToken: null,
      userType: null,
      payload: null,
      isAuthenticated: false,
      isEmployee: false,
      isDirector: false,
    });
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({ ...state, login, registerEmployee, registerExternal, logout }),
    [state, login, registerEmployee, registerExternal, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
