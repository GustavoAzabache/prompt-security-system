import { Navigate, Route, Routes } from 'react-router-dom';
import { MainLayout } from '@/components/layout/MainLayout';
import { AdminPage } from '@/pages/Admin/AdminPage';
import { DocumentsPage } from '@/pages/Documents/DocumentsPage';
import { HomePage } from '@/pages/Home/HomePage';
import { LoginPage } from '@/pages/Login/LoginPage';
import { RegisterPage } from '@/pages/Register/RegisterPage';
import { PATHS } from '@/routes/paths';
import { ProtectedRoute } from '@/routes/ProtectedRoute';

export function AppRouter() {
  return (
    <Routes>
      <Route element={<MainLayout />}>
        <Route path={PATHS.home} element={<HomePage />} />
        <Route path={PATHS.login} element={<LoginPage />} />
        <Route path={PATHS.register} element={<RegisterPage />} />
        <Route
          path={PATHS.documents}
          element={<ProtectedRoute element={<DocumentsPage />} requireEmployee />}
        />
        <Route
          path={PATHS.admin}
          element={<ProtectedRoute element={<AdminPage />} requireDirector />}
        />
        <Route path="*" element={<Navigate to={PATHS.home} replace />} />
      </Route>
    </Routes>
  );
}
