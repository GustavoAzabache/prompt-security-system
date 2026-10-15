import type { ReactElement } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { PATHS } from '@/routes/paths';

interface ProtectedRouteProps {
  element: ReactElement;
  requireEmployee?: boolean;
  requireDirector?: boolean;
}

export function ProtectedRoute({ element, requireEmployee, requireDirector }: ProtectedRouteProps) {
  const { isAuthenticated, isEmployee, isDirector } = useAuth();

  if (!isAuthenticated) return <Navigate to={PATHS.login} replace />;
  if (requireEmployee && !isEmployee) return <Navigate to={PATHS.documents} replace />;
  if (requireDirector && !isDirector) return <Navigate to={PATHS.documents} replace />;
  return element;
}
