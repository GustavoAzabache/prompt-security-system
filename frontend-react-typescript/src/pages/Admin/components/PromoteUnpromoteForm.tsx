import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { FormError } from '@/components/ui/FormError';
import { Input } from '@/components/ui/Input';
import { employeeService } from '@/services/employeeService';
import { promoteSchema, type PromoteFormValues } from '@/schemas/admin.schema';

export function PromoteUnpromoteForm() {
  const [message, setMessage] = useState<string | null>(null);
  const [submitError, setSubmitError] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    getValues,
    formState: { errors, isSubmitting },
  } = useForm<PromoteFormValues>({
    resolver: zodResolver(promoteSchema),
    defaultValues: { id: '' },
  });

  const run = async (action: 'promote' | 'unpromote') => {
    setMessage(null);
    setSubmitError(null);
    const parsed = promoteSchema.safeParse(getValues());
    if (!parsed.success) {
      setSubmitError(parsed.error.issues[0]?.message ?? 'Invalid ID');
      return;
    }
    try {
      const data =
        action === 'promote'
          ? await employeeService.promote(parsed.data.id)
          : await employeeService.unpromote(parsed.data.id);
      setMessage(data.message);
    } catch {
      setSubmitError(`${action} failed. DIRECTOR only; cannot demote last DIRECTOR.`);
    }
  };

  return (
    <Card className="p-6">
      <form onSubmit={handleSubmit(() => run('promote'))} className="flex flex-col gap-4" noValidate>
        <h2 className="text-lg font-semibold">Promote / Unpromote (DIRECTOR only)</h2>
        <FormError message={submitError} />
        <Input label="Employee ID (UUID)" placeholder="uuid" error={errors.id?.message} {...register('id')} />
        <div className="flex flex-wrap gap-2">
          <Button type="submit" disabled={isSubmitting}>
            Promote
          </Button>
          <Button type="button" variant="ghost" disabled={isSubmitting} onClick={() => void run('unpromote')}>
            Unpromote
          </Button>
        </div>
        {message && <p className="text-sm text-accent">{message}</p>}
      </form>
    </Card>
  );
}
