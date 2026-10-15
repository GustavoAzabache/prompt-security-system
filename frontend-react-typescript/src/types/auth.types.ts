export type UserType = 'employee' | 'external';

export type HierarchyLevel = 'OPERATIVE' | 'JEFE' | 'GERENTE' | 'DIRECTOR';

export interface JwtPayload {
  sub: string;
  area?: string;
  hierarchy?: HierarchyLevel;
  token_type?: string;
  exp?: number;
  iss?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RenewalAccessTokenRequest {
  refreshToken: string;
}

export interface AuthenticationResponse {
  accessToken: string;
  refreshToken: string;
}

export interface RenewalAccessTokenResponse {
  accessToken: string;
}

export interface RegisterEmployeeRequest {
  firstName: string;
  lastName: string;
  area: string;
  email: string;
  password: string;
}

export interface RegisterExternalRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}

export interface PromoteEmployeeResponse {
  message: string;
}

export interface UnpromoteEmployeeResponse {
  message: string;
}
