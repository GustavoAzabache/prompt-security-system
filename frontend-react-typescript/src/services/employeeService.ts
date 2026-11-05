import { api } from '@/lib/api';
import type {
  AuthenticationResponse,
  LoginRequest,
  RegisterEmployeeRequest,
  RenewalAccessTokenRequest,
  RenewalAccessTokenResponse,
} from '@/types/auth.types';

const BASE = '/api/v1/auth/employees';

export const employeeService = {
  register: (dto: RegisterEmployeeRequest) =>
    api.post<AuthenticationResponse>(`${BASE}/register`, dto).then((r) => r.data),
  login: (dto: LoginRequest) =>
    api.post<AuthenticationResponse>(`${BASE}/login`, dto).then((r) => r.data),
  refresh: (dto: RenewalAccessTokenRequest) =>
    api.post<RenewalAccessTokenResponse>(`${BASE}/refresh`, dto).then((r) => r.data),
};
