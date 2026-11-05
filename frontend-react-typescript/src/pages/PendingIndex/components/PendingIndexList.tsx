import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { Spinner } from '@/components/ui/Spinner';
import type { DocumentListItem } from '@/types/document.types';
import { formatBytes } from '@/utils/formatBytes';

interface PendingIndexListProps {
  documents: DocumentListItem[];
  loading: boolean;
  error: string | null;
  onRetry: () => void;
  onSelect: (doc: DocumentListItem) => void;
  indexingId: string | null;
  indexError: string | null;
  onIndex: (doc: DocumentListItem) => void;
  page: number;
  hasMore: boolean;
  onPageChange: (page: number) => void;
}

function Meta({ doc }: { doc: DocumentListItem }) {
  return (
    <>
      <p className="text-sm text-muted-foreground">
        {doc.type} · {formatBytes(doc.size)}
      </p>
      <p className="text-sm text-muted-foreground">
        Área {doc.area} · {doc.confidentialityLevel}
      </p>
    </>
  );
}

export function PendingIndexList({
  documents,
  loading,
  error,
  onRetry,
  onSelect,
  indexingId,
  indexError,
  onIndex,
  page,
  hasMore,
  onPageChange,
}: PendingIndexListProps) {
  const indexingInProgress = indexingId !== null;

  return (
    <section aria-label="Documentos por indexar" className="flex flex-col gap-4">
      <h2 className="text-lg font-semibold">Documentos pendientes ({documents.length})</h2>

      {loading && (
        <Card className="flex items-center gap-3 p-6">
          <Spinner />
          <p className="text-sm text-muted-foreground">Cargando documentos…</p>
        </Card>
      )}

      {!loading && error && (
        <Card className="flex flex-col gap-3 p-6">
          <p role="alert" className="text-sm text-destructive">
            {error}
          </p>
          <Button type="button" variant="ghost" onClick={onRetry}>
            Reintentar
          </Button>
        </Card>
      )}

      {!loading && !error && documents.length === 0 && (
        <Card className="p-6">
          <p className="text-sm text-muted-foreground">
            Todos los documentos ya están indexados.
          </p>
        </Card>
      )}

      {!loading && !error && documents.length > 0 && (
        <Card className="divide-y divide-border overflow-hidden p-0">
          {documents.map((doc) => {
            const isIndexingThis = indexingId === doc.id;
            return (
              <div
                key={doc.id}
                className="flex flex-col gap-3 p-4 odd:bg-muted/50 sm:flex-row sm:items-center sm:gap-4"
              >
                <button
                  type="button"
                  onClick={() => onSelect(doc)}
                  className="flex min-w-0 flex-1 cursor-pointer flex-col gap-1 text-left focus-visible:outline-2 focus-visible:outline-ring"
                >
                  <h3 className="truncate text-base font-semibold text-primary">{doc.title}</h3>
                  <Meta doc={doc} />
                </button>
                <Button
                  type="button"
                  disabled={indexingInProgress}
                  onClick={() => onIndex(doc)}
                  className="shrink-0"
                >
                  {isIndexingThis
                    ? 'Indexando…'
                    : indexingInProgress
                      ? 'Esperando…'
                      : 'Indexar ahora'}
                </Button>
              </div>
            );
          })}
          {indexError && (
            <div className="p-4">
              <p role="alert" className="text-sm text-destructive">
                {indexError}
              </p>
            </div>
          )}
        </Card>
      )}

      {!loading && !error && documents.length > 0 && (
        <div className="flex items-center justify-between gap-2">
          <p className="text-sm text-muted-foreground">Página {page + 1}</p>
          <div className="flex gap-2">
            <Button
              type="button"
              variant="ghost"
              disabled={page === 0 || indexingInProgress}
              onClick={() => onPageChange(page - 1)}
            >
              Anterior
            </Button>
            <Button
              type="button"
              variant="ghost"
              disabled={!hasMore || indexingInProgress}
              onClick={() => onPageChange(page + 1)}
            >
              Siguiente
            </Button>
          </div>
        </div>
      )}
    </section>
  );
}
