import { useEffect, useState } from 'react';
import { DocumentPreviewModal } from '@/components/common/DocumentPreviewModal';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { Modal } from '@/components/ui/Modal';
import { useAuth } from '@/hooks/useAuth';
import { useDocumentPreview } from '@/hooks/useDocumentPreview';
import { useDocuments } from '@/hooks/useDocuments';
import type { DocumentListItem } from '@/types/document.types';
import { ChangeConfidentialityModal } from './components/ChangeConfidentialityModal';
import { DocumentList } from './components/DocumentList';
import { UploadForm } from './components/UploadForm';

export function DocumentsPage() {
  const { payload, isEmployee } = useAuth();
  const { documents, listLoading, listError, page, hasMore, loadPage, refreshList } =
    useDocuments();
  const preview = useDocumentPreview();
  const [uploadOpen, setUploadOpen] = useState(false);
  const [confTarget, setConfTarget] = useState<DocumentListItem | null>(null);

  useEffect(() => {
    if (isEmployee) {
      refreshList().catch(() => {});
    }
  }, [isEmployee, refreshList]);

  if (!isEmployee) {
    return (
      <section className="mx-auto flex w-full max-w-2xl flex-col gap-4">
        <h1 className="text-2xl font-semibold">Documents</h1>
        <Card className="p-6">
          <p className="text-sm text-muted-foreground">
            Only employees can upload or update documents. Role: {payload?.hierarchy ?? 'external'}.
          </p>
        </Card>
      </section>
    );
  }

  return (
    <section className="mx-auto flex w-full max-w-5xl flex-col gap-6">
      <div className="flex flex-wrap items-center gap-3">
        <div className="mr-auto flex flex-col gap-1">
          <h1 className="text-2xl font-semibold">Documents</h1>
          <p className="text-sm text-muted-foreground">
            Area {payload?.area ?? '—'} · {payload?.hierarchy ?? '—'}
          </p>
        </div>
        <Button type="button" onClick={() => setUploadOpen(true)}>
          Upload file
        </Button>
      </div>

      <DocumentList
        documents={documents}
        loading={listLoading}
        error={listError}
        onRetry={() => refreshList().catch(() => {})}
        onSelect={(doc) => {
          // Los rechazados no tienen archivo (se borró): no hay preview.
          if (doc.status === 'CONTENT_REJECTED') return;
          void preview.open(doc);
        }}
        page={page}
        hasMore={hasMore}
        onPageChange={(p) => void loadPage(p).catch(() => {})}
        canChangeConfidentiality={payload?.hierarchy !== 'OPERATIVE'}
        onChangeConfidentiality={setConfTarget}
      />

      {uploadOpen && (
        <Modal title="Upload document" onClose={() => setUploadOpen(false)}>
          <UploadForm
            onUploaded={() => refreshList().catch(() => {})}
            onClose={() => setUploadOpen(false)}
          />
        </Modal>
      )}

      {preview.status !== 'idle' && (
        <DocumentPreviewModal
          status={preview.status}
          preview={preview.preview}
          reasons={preview.reasons}
          onClose={preview.close}
        />
      )}

      {confTarget && (
        <ChangeConfidentialityModal
          doc={confTarget}
          onClose={() => setConfTarget(null)}
          onChanged={() => refreshList().catch(() => {})}
        />
      )}
    </section>
  );
}
