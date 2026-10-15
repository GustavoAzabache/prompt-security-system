import { Link } from 'react-router-dom';
import { Card } from '@/components/ui/Card';
import { useAuth } from '@/hooks/useAuth';
import { PATHS } from '@/routes/paths';

export function HomePage() {
  const { isAuthenticated, isEmployee, payload } = useAuth();

  return (
    <section className="flex flex-col gap-6">
      <div className="flex flex-col gap-2">
        <h1 className="text-3xl font-semibold text-foreground sm:text-4xl">Prompt Security System</h1>
        <p className="max-w-2xl text-base leading-6 text-muted-foreground">
          Secure document upload with confidentiality control. Employees manage files; external
          users have no document dashboard.
        </p>
      </div>
      <Card className="flex flex-col gap-3 p-6">
        {isAuthenticated ? (
          <>
            <p className="text-sm text-foreground">
              Signed in{isEmployee ? ` · area ${payload?.area ?? '—'} · ${payload?.hierarchy ?? '—'}` : ' · external user'}.
            </p>
            {isEmployee ? (
              <Link to={PATHS.documents} className="text-sm font-medium text-secondary underline">
                Go to documents
              </Link>
            ) : (
              <p className="text-sm text-muted-foreground">
                External accounts cannot access documents. This home page is your landing page.
              </p>
            )}
          </>
        ) : (
          <>
            <p className="text-sm text-muted-foreground">Sign in to continue.</p>
            <div className="flex flex-wrap gap-2">
              <Link to={PATHS.login} className="rounded-md bg-primary px-4 py-2 text-sm font-medium text-on-primary">
                Login
              </Link>
              <Link to={PATHS.register} className="rounded-md border border-border px-4 py-2 text-sm font-medium">
                Register
              </Link>
            </div>
          </>
        )}
      </Card>
    </section>
  );
}
