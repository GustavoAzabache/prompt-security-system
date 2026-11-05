import { useEffect, useState } from 'react';
import { DocumentPreviewModal } from '@/components/common/DocumentPreviewModal';
import { Card } from '@/components/ui/Card';
import { useAuth } from '@/hooks/useAuth';
import { useDocumentPreview } from '@/hooks/useDocumentPreview';
import { usePendingIndex } from '@/hooks/usePendingIndex';
import type { DocumentListItem } from '@/types/document.types';
import { PendingIndexList } from './components/PendingIndexList';

export function PendingIndexPage() {
  const { payload, isEmployee } = useAuth();
  const {
    documents,
    listLoading,
    listError,
    page,
    hasMore,
    indexingId,
    indexError,
    loadPage,
    refreshList,
    indexDocument,
  } = usePendingIndex();
  const preview = useDocumentPreview();
  const [indexedMsg, setIndexedMsg] = useState<string | null>(null);

  useEffect(() => {
    if (isEmployee) {
      refreshList().catch(() => {});
    }
  }, [isEmployee, refreshList]);

  if (!isEmployee) {
    return (
      <section className="mx-auto flex w-full max-w-2xl flex-col gap-4">
        <h1 className="text-2xl font-semibold">Documentos por indexar</h1>
        <Card className="p-6">
          <p className="text-sm text-muted-foreground">
            Solo los empleados pueden indexar documentos. Rol: {payload?.hierarchy ?? 'external'}.
          </p>
        </Card>
      </section>
    );
  }

  const handleIndex = async (doc: DocumentListItem) => {
    setIndexedMsg(null);
    try {
      const result = await indexDocument(doc.id);
      setIndexedMsg(
        result.status === 'ALREADY_INDEXED'
          ? `"${doc.title}" ya estaba indexado. No se gastó cuota.`
          : `Indexado: ${result.chunkCount} fragmentos de "${doc.title}".`,
      );
    } catch {
      // El error ya quedó en indexError dentro del hook
    }
  };

  return (
    <section className="mx-auto flex w-full max-w-5xl flex-col gap-6">
      <div className="mr-auto flex flex-col gap-1">
        <h1 className="text-2xl font-semibold">Documentos por indexar</h1>
        <p className="text-sm text-muted-foreground">
          Documentos aprobados por el escaneo que aún no están disponibles para consulta.
        </p>
      </div>

      {indexedMsg && (
        <Card className="p-4">
          <p className="text-sm text-accent">{indexedMsg}</p>
        </Card>
      )}

      <PendingIndexList
        documents={documents}
        loading={listLoading}
        error={listError}
        onRetry={() => refreshList().catch(() => {})}
        onSelect={(doc) => void preview.open(doc)}
        indexingId={indexingId}
        indexError={indexError}
        onIndex={(doc) => void handleIndex(doc)}
        page={page}
        hasMore={hasMore}
        onPageChange={(p) => void loadPage(p).catch(() => {})}
      />

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
