import { useState } from 'react';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { Spinner } from '@/components/ui/Spinner';
import { CONFIDENTIALITY_LABELS } from '@/constants/labels';
import type { DocumentListItem, DocumentStatus } from '@/types/document.types';
import { formatBytes } from '@/utils/formatBytes';

type View = 'lines' | 'grid';

/** Etiqueta en español; si el backend manda un nivel desconocido, se muestra tal cual. */
function confidentialityLabel(level: string): string {
  return (
    CONFIDENTIALITY_LABELS[level as keyof typeof CONFIDENTIALITY_LABELS] ?? level
  );
}

const STATUS_BADGES: Record<DocumentStatus, { label: string; className: string } | null> = {
  CONTENT_READY_TO_READ: {
    label: 'Pendiente de indexar',
    className: 'bg-secondary text-on-secondary',
  },
  READY_TO_READ_AND_CONSULT: {
    label: 'Listo para consultar',
    className: 'bg-accent text-on-accent',
  },
  INDEXING: {
    label: 'Indexando…',
    className: 'bg-muted text-muted-foreground',
  },
  PENDING: null,
  CONTENT_REJECTED: {
    label: 'Rechazado',
    className: 'bg-destructive text-on-destructive',
  },
};

function StatusBadge({ status }: { status: DocumentStatus }) {
  const badge = STATUS_BADGES[status];
  if (!badge) return null;
  return (
    <span
      className={`inline-flex shrink-0 items-center whitespace-nowrap rounded-full px-2 py-0.5 text-xs font-medium ${badge.className}`}
    >
      {badge.label}
    </span>
  );
}

interface DocumentListProps {
  documents: DocumentListItem[];
  loading: boolean;
  error: string | null;
  onRetry: () => void;
  onSelect: (doc: DocumentListItem) => void;
  page: number;
  hasMore: boolean;
  onPageChange: (page: number) => void;
  /** Habilita el engranaje de confidencialidad según el rol del usuario. */
  canChangeConfidentiality: boolean;
  onChangeConfidentiality: (doc: DocumentListItem) => void;
}

function Meta({ doc }: { doc: DocumentListItem }) {
  return (
    <>
      <p className="text-sm text-muted-foreground">
        {doc.type} · {formatBytes(doc.size)}
      </p>
      <p className="text-sm text-muted-foreground">
        Área: {doc.area} · {confidentialityLabel(doc.confidentialityLevel)}
      </p>
    </>
  );
}

/** Motivo de rechazo: una sola línea cortada, el tooltip muestra el texto completo. */
function RejectionReason({ doc }: { doc: DocumentListItem }) {
  if (doc.status !== 'CONTENT_REJECTED' || !doc.findings?.length) return null;

  const reason = doc.findings.join(' · ');

  return (
    <p
      className="truncate text-sm text-destructive"
      title={reason}
    >
      Motivo: {reason}
    </p>
  );
}

interface RowBodyProps {
  doc: DocumentListItem;
}

function RowBody({ doc }: RowBodyProps) {
  return (
    <>
      <span className="flex flex-wrap items-center gap-2">
        <h3 className="text-base font-semibold text-primary">{doc.title}</h3>
        <StatusBadge status={doc.status} />
      </span>
      <Meta doc={doc} />
      <RejectionReason doc={doc} />
    </>
  );
}

/**
 * Los documentos rechazados no son clicables: su archivo físico fue
 * eliminado, así que no hay preview que abrir.
 */
function isSelectable(doc: DocumentListItem) {
  return doc.status !== 'CONTENT_REJECTED';
}

/** Estados en los que el backend acepta cambiar la confidencialidad. */
function isConfidentialityChangeable(doc: DocumentListItem) {
  return (
    doc.status === 'CONTENT_READY_TO_READ' || doc.status === 'READY_TO_READ_AND_CONSULT'
  );
}

/** Engranaje: cambia la confidencialidad sin tener que copiar el UUID. */
function GearButton({ doc, onClick }: { doc: DocumentListItem; onClick: (doc: DocumentListItem) => void }) {
  return (
    <button
      type="button"
      aria-label={`Cambiar confidencialidad de ${doc.title}`}
      title="Cambiar confidencialidad"
      onClick={() => onClick(doc)}
      className="mr-2 min-h-11 min-w-11 shrink-0 cursor-pointer rounded-md p-2.5 text-muted-foreground transition-colors hover:bg-muted hover:text-foreground focus-visible:outline-2 focus-visible:outline-ring"
    >
      <svg viewBox="0 0 24 24" aria-hidden="true" className="h-5 w-5" fill="currentColor">
        <path d="M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z" />
      </svg>
    </button>
  );
}

export function DocumentList({
  documents,
  loading,
  error,
  onRetry,
  onSelect,
  page,
  hasMore,
  onPageChange,
  canChangeConfidentiality,
  onChangeConfidentiality,
}: DocumentListProps) {
  const [view, setView] = useState<View>('lines');

  // Espejo de canChangeConfidentiality del backend: no ofrecemos lo que el
  // servidor rechaza (403), y los PENDING/INDEXING/RECHAZADOS no aplican.
  const showGear = (doc: DocumentListItem) =>
    canChangeConfidentiality && isConfidentialityChangeable(doc);

  return (
    <section aria-label="Lista de documentos" className="flex flex-col gap-4">
      <div className="flex items-center justify-between gap-2">
        <h2 className="text-lg font-semibold">Documentos ({documents.length})</h2>
        <div role="group" aria-label="Modo de vista" className="flex gap-2">
          <button
            type="button"
            aria-pressed={view === 'lines'}
            onClick={() => setView('lines')}
            className={`inline-flex min-h-11 cursor-pointer items-center gap-2 rounded-md border px-3 py-2 text-sm focus-visible:outline-2 focus-visible:outline-ring ${view === 'lines' ? 'border-primary bg-primary text-on-primary' : 'border-border bg-card hover:bg-muted'}`}
          >
            <svg viewBox="0 0 20 20" aria-hidden="true" className="h-4 w-4" fill="currentColor">
              <rect x="2" y="4" width="16" height="2" rx="1" />
              <rect x="2" y="9" width="16" height="2" rx="1" />
              <rect x="2" y="14" width="16" height="2" rx="1" />
            </svg>
            Líneas
          </button>
          <button
            type="button"
            aria-pressed={view === 'grid'}
            onClick={() => setView('grid')}
            className={`inline-flex min-h-11 cursor-pointer items-center gap-2 rounded-md border px-3 py-2 text-sm focus-visible:outline-2 focus-visible:outline-ring ${view === 'grid' ? 'border-primary bg-primary text-on-primary' : 'border-border bg-card hover:bg-muted'}`}
          >
            <svg viewBox="0 0 20 20" aria-hidden="true" className="h-4 w-4" fill="currentColor">
              <rect x="2" y="2" width="7" height="7" rx="1" />
              <rect x="11" y="2" width="7" height="7" rx="1" />
              <rect x="2" y="11" width="7" height="7" rx="1" />
              <rect x="11" y="11" width="7" height="7" rx="1" />
            </svg>
            Mosaico
          </button>
        </div>
      </div>

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
          <p className="text-sm text-muted-foreground">Aún no hay documentos. Sube el primero.</p>
        </Card>
      )}

      {!loading && !error && documents.length > 0 && view === 'lines' && (
        <Card className="divide-y divide-border overflow-hidden p-0">
          {documents.map((doc) => (
            <div key={doc.id} className="flex items-center gap-1 odd:bg-muted/50">
              {isSelectable(doc) ? (
                <button
                  type="button"
                  onClick={() => onSelect(doc)}
                  className="flex min-w-0 flex-1 cursor-pointer flex-col gap-1 p-4 text-left transition-colors hover:bg-muted focus-visible:outline-2 focus-visible:outline-ring"
                >
                  <RowBody doc={doc} />
                </button>
              ) : (
                <div className="flex min-w-0 flex-1 flex-col gap-1 p-4 text-left">
                  <RowBody doc={doc} />
                </div>
              )}
              {showGear(doc) && <GearButton doc={doc} onClick={onChangeConfidentiality} />}
            </div>
          ))}
        </Card>
      )}

      {!loading && !error && documents.length > 0 && view === 'grid' && (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {documents.map((doc) => (
            <div
              key={doc.id}
              className={`flex items-start gap-1 rounded-lg border border-border border-t-4 bg-card shadow-sm ${
                isSelectable(doc) ? 'border-t-primary transition-colors' : 'border-t-destructive'
              }`}
            >
              {isSelectable(doc) ? (
                <button
                  type="button"
                  onClick={() => onSelect(doc)}
                  className="flex min-w-0 flex-1 cursor-pointer flex-col gap-1 p-4 text-left transition-colors hover:bg-muted focus-visible:outline-2 focus-visible:outline-ring"
                >
                  <RowBody doc={doc} />
                </button>
              ) : (
                <div className="flex min-w-0 flex-1 flex-col gap-1 p-4 text-left">
                  <RowBody doc={doc} />
                </div>
              )}
              {showGear(doc) && <GearButton doc={doc} onClick={onChangeConfidentiality} />}
            </div>
          ))}
        </div>
      )}
      {!loading && !error && documents.length > 0 && (
        <div className="flex items-center justify-between gap-2">
          <p className="text-sm text-muted-foreground">Página {page + 1}</p>
          <div className="flex gap-2">
            <Button
              type="button"
              variant="ghost"
              disabled={page === 0}
              onClick={() => onPageChange(page - 1)}
            >
              Anterior
            </Button>
            <Button
              type="button"
              variant="ghost"
              disabled={!hasMore}
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
