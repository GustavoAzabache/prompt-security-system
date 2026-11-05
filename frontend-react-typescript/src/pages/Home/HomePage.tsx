import { Link, Navigate } from 'react-router-dom';
import { Card } from '@/components/ui/Card';
import { useAuth } from '@/hooks/useAuth';
import { PATHS } from '@/routes/paths';

export function HomePage() {
  const { isAuthenticated, isEmployee } = useAuth();

  if (isAuthenticated && isEmployee) {
    return <Navigate to={PATHS.documents} replace />;
  }

  return (
    <section className="flex flex-col gap-6">
      <div className="flex flex-col gap-2">
        <h1 className="text-3xl font-semibold text-foreground sm:text-4xl">Prompt Security System</h1>
        <p className="max-w-2xl text-base leading-6 text-muted-foreground">
          Subida segura de documentos con control de confidencialidad. Los empleados gestionan los
          archivos; los usuarios externos hacen preguntas sobre documentos públicos.
        </p>
      </div>
      <Card className="flex flex-col gap-3 p-6">
        {isAuthenticated ? (
          <>
            <p className="text-sm text-foreground">Sesión iniciada · usuario externo.</p>
            <Link to={PATHS.ask} className="text-sm font-medium text-secondary underline">
              Consultar los documentos
            </Link>
          </>
        ) : (
          <>
            <p className="text-sm text-muted-foreground">Inicia sesión para continuar.</p>
            <div className="flex flex-wrap gap-2">
              <Link to={PATHS.login} className="rounded-md bg-primary px-4 py-2 text-sm font-medium text-on-primary">
                Iniciar sesión
              </Link>
              <Link to={PATHS.register} className="rounded-md border border-border px-4 py-2 text-sm font-medium">
                Registrarse
              </Link>
            </div>
          </>
        )}
      </Card>
    </section>
  );
}
