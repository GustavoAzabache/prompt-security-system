import { PromoteUnpromoteForm } from './components/PromoteUnpromoteForm';

export function AdminPage() {
  return (
    <section className="mx-auto flex w-full max-w-2xl flex-col gap-4">
      <h1 className="text-2xl font-semibold">Admin</h1>
      <PromoteUnpromoteForm />
    </section>
  );
}
