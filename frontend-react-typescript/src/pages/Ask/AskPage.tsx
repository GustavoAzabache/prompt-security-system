import { useState } from 'react';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { DocumentPreviewModal } from '@/components/common/DocumentPreviewModal';
import { Spinner } from '@/components/ui/Spinner';
import { useAuth } from '@/hooks/useAuth';
import { useDocumentPreview } from '@/hooks/useDocumentPreview';
import { useRag } from '@/hooks/useRag';
import type { RagQueryFormValues } from '@/schemas/rag.schema';
import { RagAnswer } from './components/RagAnswer';
import { RagQueryForm } from './components/RagQueryForm';

export function AskPage() {
  const { isEmployee } = useAuth();
  const { askStatus, answer, askError, ask, cancelAsk } = useRag();
  const preview = useDocumentPreview();
  const [lastQuery, setLastQuery] = useState<RagQueryFormValues | null>(null);

  const onSubmit = (values: RagQueryFormValues) => {
    setLastQuery(values);
    void ask(values).catch(() => {});
  };

  return (
    <section className="mx-auto flex w-full max-w-3xl flex-col gap-6">
      <div className="flex flex-col gap-1">
        <h1 className="text-2xl font-semibold">Ask the documents</h1>
        <p className="text-sm text-muted-foreground">
          {isEmployee
            ? 'Answers come from documents you are allowed to see, with cited sources.'
            : 'External view: answers only use PUBLIC documents.'}
        </p>
      </div>

      <RagQueryForm
        asking={askStatus === 'asking'}
        submitError={askStatus === 'error' ? askError : null}
        onSubmit={onSubmit}
        onCancel={cancelAsk}
      />

      {askStatus === 'error' && lastQuery && (
        <div className="flex flex-wrap gap-2">
          <Button type="button" variant="secondary" onClick={() => onSubmit(lastQuery)}>
            Retry
          </Button>
        </div>
      )}

      {askStatus === 'asking' && (
        <Card className="flex items-center gap-3 p-6" aria-busy="true">
          <Spinner />
          <p className="text-sm text-muted-foreground">
            Thinking… this can take up to a minute. You can cancel anytime.
          </p>
        </Card>
      )}

      {askStatus === 'success' && answer && (
        <RagAnswer
          answer={answer}
          onViewSource={(documentId, title) => void preview.open({ id: documentId, title })}
        />
      )}

      {preview.status !== 'idle' && (
        <DocumentPreviewModal
          status={preview.status}
          preview={preview.preview}
          reasons={preview.reasons}
          onClose={preview.close}
        />
      )}
    </section>
  );
}
