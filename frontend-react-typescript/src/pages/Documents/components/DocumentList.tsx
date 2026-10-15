import { useState } from 'react';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { Spinner } from '@/components/ui/Spinner';
import type { DocumentListItem } from '@/types/document.types';
import { formatBytes } from '@/utils/formatBytes';

type View = 'lines' | 'grid';

interface DocumentListProps {
  documents: DocumentListItem[];
  loading: boolean;
  error: string | null;
  onRetry: () => void;
  onSelect: (doc: DocumentListItem) => void;
}

function Meta({ doc }: { doc: DocumentListItem }) {
  return (
    <>
      <p className="text-sm text-muted-foreground">
        {doc.type} · {formatBytes(doc.size)}
      </p>
      <p className="text-sm text-muted-foreground">
        Area {doc.area} · {doc.confidentialityLevel}
      </p>
    </>
  );
}

export function DocumentList({ documents, loading, error, onRetry, onSelect }: DocumentListProps) {
  const [view, setView] = useState<View>('lines');

  return (
    <section aria-label="Document list" className="flex flex-col gap-4">
      <div className="flex items-center justify-between gap-2">
        <h2 className="text-lg font-semibold">Documents ({documents.length})</h2>
        <div role="group" aria-label="View mode" className="flex gap-2">
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
            Lines
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
            Mosaic
          </button>
        </div>
      </div>

      {loading && (
        <Card className="flex items-center gap-3 p-6">
          <Spinner />
          <p className="text-sm text-muted-foreground">Loading documents…</p>
        </Card>
      )}

      {!loading && error && (
        <Card className="flex flex-col gap-3 p-6">
          <p role="alert" className="text-sm text-destructive">
            {error}
          </p>
          <Button type="button" variant="ghost" onClick={onRetry}>
            Retry
          </Button>
        </Card>
      )}

      {!loading && !error && documents.length === 0 && (
        <Card className="p-6">
          <p className="text-sm text-muted-foreground">No documents yet. Upload the first one.</p>
        </Card>
      )}

      {!loading && !error && documents.length > 0 && view === 'lines' && (
        <Card className="divide-y divide-border overflow-hidden p-0">
          {documents.map((doc) => (
            <button
              key={doc.id}
              type="button"
              onClick={() => onSelect(doc)}
              className="flex w-full cursor-pointer flex-col gap-1 p-4 text-left transition-colors odd:bg-muted/50 hover:bg-muted focus-visible:outline-2 focus-visible:outline-ring"
            >
              <h3 className="text-base font-semibold text-primary">{doc.title}</h3>
              <Meta doc={doc} />
            </button>
          ))}
        </Card>
      )}

      {!loading && !error && documents.length > 0 && view === 'grid' && (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {documents.map((doc) => (
            <button
              key={doc.id}
              type="button"
              onClick={() => onSelect(doc)}
              className="flex cursor-pointer flex-col gap-1 rounded-lg border border-border border-t-4 border-t-primary bg-card p-4 text-left shadow-sm transition-colors hover:bg-muted focus-visible:outline-2 focus-visible:outline-ring"
            >
              <h3 className="truncate text-base font-semibold text-primary">{doc.title}</h3>
              <Meta doc={doc} />
            </button>
          ))}
        </div>
      )}
    </section>
  );
}
