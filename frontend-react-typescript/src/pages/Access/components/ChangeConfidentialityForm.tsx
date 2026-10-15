import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { CONFIDENTIALITY_LEVELS } from '@/constants/roles';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { FormError } from '@/components/ui/FormError';
import { Input } from '@/components/ui/Input';
import { Select } from '@/components/ui/Select';
import { useDocuments } from '@/hooks/useDocuments';
import { useRag } from '@/hooks/useRag';
import {
  changeConfidentialitySchema,
  type ChangeConfidentialityFormValues,
} from '@/schemas/document.schema';

export function ChangeConfidentialityForm() {
  const { changeConfidentiality, loading } = useDocuments();
  const { ingest, ingesting } = useRag();
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [lastDocId, setLastDocId] = useState<string | null>(null);
  const [reindexMsg, setReindexMsg] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ChangeConfidentialityFormValues>({
    resolver: zodResolver(changeConfidentialitySchema),
    defaultValues: { id: '', confidentialityLevel: 'PUBLIC' },
  });

  const onSubmit = handleSubmit(async (values) => {
    setSubmitError(null);
    setSuccess(null);
    setReindexMsg(null);
    try {
      const data = await changeConfidentiality(values.id, {
        confidentialityLevel: values.confidentialityLevel,
      });
      setSuccess(data.message);
      setLastDocId(values.id);
    } catch {
      setSubmitError('Update failed. Check permissions and document ID.');
    }
  });

  const onReindex = async () => {
    if (!lastDocId) return;
    setReindexMsg(null);
    try {
      const data = await ingest(lastDocId, true);
      setReindexMsg(`Re-indexed: ${data.status} · ${data.chunkCount} chunks.`);
    } catch {
      setReindexMsg('Re-index failed.');
    }
  };

  return (
    <Card className="p-6">
      <form onSubmit={onSubmit} className="flex flex-col gap-4" noValidate>
        <h2 className="text-lg font-semibold">Change confidentiality</h2>
        <FormError message={submitError} />
        <Input label="Document ID" placeholder="UUID" error={errors.id?.message} {...register('id')} />
        <Select
          label="Level"
          error={errors.confidentialityLevel?.message}
          {...register('confidentialityLevel')}
        >
          {CONFIDENTIALITY_LEVELS.map((l) => (
            <option key={l} value={l}>
              {l}
            </option>
          ))}
        </Select>
        <Button type="submit" disabled={loading}>
          {loading ? 'Updating…' : 'Update'}
        </Button>
        {success && (
          <div className="flex flex-col gap-2">
            <p className="text-sm text-accent">{success}</p>
            <p className="text-sm text-muted-foreground">
              The search index is now stale for this document.
            </p>
            <div>
              <Button type="button" variant="secondary" disabled={ingesting} onClick={onReindex}>
                {ingesting ? 'Re-indexing…' : 'Re-index now (force)'}
              </Button>
            </div>
            {reindexMsg && <p className="text-sm text-accent">{reindexMsg}</p>}
          </div>
        )}
      </form>
    </Card>
  );
}
