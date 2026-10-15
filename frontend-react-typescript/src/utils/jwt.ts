import type { JwtPayload } from '@/types/auth.types';

export function decodeJwt(token: string | null): JwtPayload | null {
  if (!token) return null;
  const parts = token.split('.');
  if (parts.length < 2 || !parts[1]) return null;
  try {
    const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
    const json = atob(base64);
    return JSON.parse(json) as JwtPayload;
  } catch {
    return null;
  }
}

export function isEmployee(payload: JwtPayload | null): boolean {
  return !!payload?.area;
}

export function isDirector(payload: JwtPayload | null): boolean {
  return payload?.hierarchy === 'DIRECTOR';
}

export function canUpdateDocuments(payload: JwtPayload | null): boolean {
  return !!payload?.area && payload.hierarchy !== 'OPERATIVE';
}
