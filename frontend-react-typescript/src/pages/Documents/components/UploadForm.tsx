import { useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { FormError } from '@/components/ui/FormError';
import { useDocuments } from '@/hooks/useDocuments';
import { useRag } from '@/hooks/useRag';
import { uploadDocumentSchema } from '@/schemas/document.schema';

interface UploadFormProps {
  onUploaded?: () => void;
  onClose?: () => void;
}

export function UploadForm({ onUploaded, onClose }: UploadFormProps) {
  const { upload, loading } = useDocuments();
  const { ingest, ingesting } = useRag();
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [uploadedId, setUploadedId] = useState<string | null>(null);
  const [ingestMsg, setIngestMsg] = useState<string | null>(null);
  const [inputKey, setInputKey] = useState(0);
  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<{ file: File }>({
    resolver: zodResolver(uploadDocumentSchema),
    defaultValues: { file: undefined as unknown as File },
  });

  const onSubmit = handleSubmit(async (values) => {
    setSubmitError(null);
    setSuccess(null);
    setIngestMsg(null);
    try {
      const data = await upload(values.file);
      setSuccess(`${data.name} (${data.type}, ${data.size} bytes)`);
      setUploadedId(data.id);
      reset({ file: undefined as unknown as File });
      setInputKey((k) => k + 1);
      onUploaded?.();
    } catch {
      setSubmitError('Upload failed. Only employees can upload PDF files.');
    }
  });

  const onIndexNow = async () => {
    if (!uploadedId) return;
    setIngestMsg(null);
    try {
      const data = await ingest(uploadedId, false);
      setIngestMsg(
        data.status === 'ALREADY_INDEXED'
          ? 'Already indexed. No quota spent.'
          : `Indexed: ${data.chunkCount} chunks.`,
      );
    } catch {
      setIngestMsg('Indexing failed.');
    }
  };

  return (
    <Card className="p-6">
      <form onSubmit={onSubmit} className="flex flex-col gap-4" noValidate>
        <h2 className="text-lg font-semibold">Upload document</h2>
        <FormError message={submitError} />
        <div className="flex flex-col gap-1.5">
          <label htmlFor="file" className="text-sm font-medium text-foreground">
            File (PDF only · max 10MB)
          </label>
          <Controller
            name="file"
            control={control}
            render={({ field: { onChange, onBlur, ref } }) => (
              <input
                key={inputKey}
                id="file"
                type="file"
                accept=".pdf"
                ref={ref}
                onBlur={onBlur}
                onChange={(e) => onChange(e.target.files?.[0])}
                aria-invalid={!!errors.file}
                aria-describedby={errors.file ? 'file-error' : undefined}
                className="min-h-11 rounded-md border border-border bg-card px-3 py-2 text-sm file:mr-3 file:rounded-md file:border-0 file:bg-muted file:px-3 file:py-2 file:text-sm"
              />
            )}
          />
          {errors.file && (
            <p id="file-error" role="alert" className="text-sm text-destructive">
              {errors.file.message}
            </p>
          )}
        </div>
        <Button type="submit" disabled={loading}>
          {loading ? 'Uploading…' : 'Upload'}
        </Button>
        {success && (
          <div className="flex flex-col gap-2">
            <p className="text-sm text-accent">{success}</p>
            <div className="flex flex-wrap gap-2">
              <Button type="button" variant="secondary" disabled={ingesting} onClick={onIndexNow}>
                {ingesting ? 'Indexing…' : 'Index now'}
              </Button>
              {onClose && (
                <Button type="button" variant="ghost" onClick={onClose}>
                  Close
                </Button>
              )}
            </div>
            {ingestMsg && <p className="text-sm text-accent">{ingestMsg}</p>}
          </div>
        )}
      </form>
    </Card>
  );
}
