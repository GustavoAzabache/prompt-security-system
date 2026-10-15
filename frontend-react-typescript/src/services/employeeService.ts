import { api } from '@/lib/api';
import type {
  AuthenticationResponse,
  LoginRequest,
  PromoteEmployeeResponse,
  RegisterEmployeeRequest,
  RenewalAccessTokenRequest,
  RenewalAccessTokenResponse,
  UnpromoteEmployeeResponse,
} from '@/types/auth.types';

const BASE = '/api/v1/auth/employees';

export const employeeService = {
  register: (dto: RegisterEmployeeRequest) =>
    api.post<AuthenticationResponse>(`${BASE}/register`, dto).then((r) => r.data),
  login: (dto: LoginRequest) =>
    api.post<AuthenticationResponse>(`${BASE}/login`, dto).then((r) => r.data),
  refresh: (dto: RenewalAccessTokenRequest) =>
    api.post<RenewalAccessTokenResponse>(`${BASE}/refresh`, dto).then((r) => r.data),
  promote: (id: string) =>
    api.patch<PromoteEmployeeResponse>(`${BASE}/${id}/promote`).then((r) => r.data),
  unpromote: (id: string) =>
    api.patch<UnpromoteEmployeeResponse>(`${BASE}/${id}/unpromote`).then((r) => r.data),
};
