import { Link, Outlet } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { PATHS } from '@/routes/paths';

const navLink =
  'rounded-md px-3 py-2 text-sm text-on-primary/90 hover:bg-white/10 hover:text-on-primary focus-visible:outline-2 focus-visible:outline-white';

export function MainLayout() {
  const { isAuthenticated, isEmployee, isDirector, logout } = useAuth();

  return (
    <div className="flex min-h-svh flex-col bg-background text-foreground">
      <header className="bg-primary text-on-primary shadow">
        <nav className="mx-auto flex w-full max-w-5xl flex-wrap items-center gap-2 px-4 py-3" aria-label="Main">
          <Link to={PATHS.home} className="mr-auto text-sm font-semibold tracking-wide text-on-primary">
            Prompt Security
          </Link>
          {isAuthenticated ? (
            <>
              {isEmployee && (
                <Link to={PATHS.documents} className={navLink}>
                  Documents
                </Link>
              )}
              {isDirector && (
                <Link to={PATHS.admin} className={navLink}>
                  Admin
                </Link>
              )}
              <button
                type="button"
                onClick={logout}
                className="min-h-11 cursor-pointer rounded-md border border-white/30 px-3 py-2 text-sm text-on-primary hover:bg-white/10 focus-visible:outline-2 focus-visible:outline-white"
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <Link to={PATHS.login} className={navLink}>
                Login
              </Link>
              <Link to={PATHS.register} className={navLink}>
                Register
              </Link>
            </>
          )}
        </nav>
      </header>
      <main className="mx-auto w-full max-w-5xl flex-1 px-4 py-8">
        <Outlet />
      </main>
    </div>
  );
}
