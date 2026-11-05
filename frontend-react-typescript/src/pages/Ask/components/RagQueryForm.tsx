import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { FormError } from '@/components/ui/FormError';
import { ragQuerySchema, type RagQueryFormInput, type RagQueryFormValues } from '@/schemas/rag.schema';

interface RagQueryFormProps {
  asking: boolean;
  submitError: string | null;
  onSubmit: (values: RagQueryFormValues) => void;
  onCancel: () => void;
}

export function RagQueryForm({ asking, submitError, onSubmit, onCancel }: RagQueryFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RagQueryFormInput>({
    resolver: zodResolver(ragQuerySchema),
    defaultValues: { query: '', topK: 4 },
  });

  const onSubmitForm = handleSubmit((values) => onSubmit(values as RagQueryFormValues));

  return (
    <Card className="p-6">
      <form onSubmit={onSubmitForm} className="flex flex-col gap-4" noValidate>
        <FormError message={submitError} />
        <div className="flex flex-col gap-1.5">
          <label htmlFor="rag-query" className="text-sm font-medium text-foreground">
            Tu pregunta
          </label>
          <textarea
            id="rag-query"
            rows={3}
            maxLength={2000}
            placeholder="p. ej. ¿Cuál es la política de vacaciones?"
            aria-invalid={!!errors.query}
            aria-describedby={errors.query ? 'rag-query-error' : undefined}
            {...register('query')}
            className="min-h-11 rounded-md border border-border bg-card px-3 py-2 text-base text-card-foreground placeholder:text-muted-foreground focus-visible:outline-2 focus-visible:outline-offset-1 focus-visible:outline-ring"
          />
          {errors.query && (
            <p id="rag-query-error" role="alert" className="text-sm text-destructive">
              {errors.query.message}
            </p>
          )}
        </div>
        <details className="text-sm">
          <summary className="cursor-pointer font-medium text-primary">Opciones avanzadas</summary>
          <div className="mt-2 flex max-w-40 flex-col gap-1.5">
            <label htmlFor="rag-topk" className="text-sm font-medium text-foreground">
              Fuentes a recuperar (1–10)
            </label>
            <input
              id="rag-topk"
              type="number"
              min={1}
              max={10}
              {...register('topK')}
              className="min-h-11 rounded-md border border-border bg-card px-3 py-2 text-base"
            />
            {errors.topK && (
              <p role="alert" className="text-sm text-destructive">
                {errors.topK.message}
              </p>
            )}
          </div>
        </details>
        <div className="flex flex-wrap gap-2">
          <Button type="submit" disabled={asking}>
            {asking ? 'Consultando…' : 'Consultar'}
          </Button>
          {asking && (
            <Button type="button" variant="ghost" onClick={onCancel}>
              Cancelar
            </Button>
          )}
        </div>
      </form>
    </Card>
  );
}
