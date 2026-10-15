import { ChangeConfidentialityForm } from './components/ChangeConfidentialityForm';

export function AccessPage() {
  return (
    <section className="mx-auto flex w-full max-w-2xl flex-col gap-4">
      <div className="flex flex-col gap-1">
        <h1 className="text-2xl font-semibold">Access control</h1>
        <p className="text-sm text-muted-foreground">
          Change a document&apos;s confidentiality level. Re-index afterwards so search sees the
          new level.
        </p>
      </div>
      <ChangeConfidentialityForm />
    </section>
  );
}
