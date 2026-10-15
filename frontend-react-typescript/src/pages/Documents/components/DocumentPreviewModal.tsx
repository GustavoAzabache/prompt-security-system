import { Button } from '@/components/ui/Button';
import { Modal } from '@/components/ui/Modal';
import { Spinner } from '@/components/ui/Spinner';
import type { DocumentPreview } from '@/types/document.types';

interface DocumentPreviewModalProps {
  status: 'loading' | 'allowed' | 'denied';
  preview: DocumentPreview | null;
  reasons: string[];
  onClose: () => void;
}

function isInlineViewable(contentType: string): boolean {
  return (
    contentType === 'application/pdf' ||
    contentType.startsWith('text/') ||
    contentType.startsWith('image/')
  );
}

export function DocumentPreviewModal({ status, preview, reasons, onClose }: DocumentPreviewModalProps) {
  return (
    <Modal title={preview?.title ?? 'Document'} onClose={onClose} wide>
      {status === 'loading' && (
        <div className="flex items-center gap-3 py-8">
          <Spinner />
          <p className="text-sm text-muted-foreground">Asking the server if you can view it…</p>
        </div>
      )}

      {status === 'denied' && (
        <div className="flex flex-col gap-3">
          <p role="alert" className="text-sm font-medium text-destructive">
            You cannot view this document.
          </p>
          <ul className="flex list-disc flex-col gap-1 pl-5 text-sm text-muted-foreground">
            {reasons.map((r) => (
              <li key={r}>{r}</li>
            ))}
          </ul>
          <Button type="button" variant="ghost" onClick={onClose}>
            Close
          </Button>
        </div>
      )}

      {status === 'allowed' && preview && isInlineViewable(preview.contentType) && (
        <iframe
          src={preview.url}
          title={preview.title}
          className="h-[70vh] w-full rounded-md border border-border bg-card"
        />
      )}

      {status === 'allowed' && preview && !isInlineViewable(preview.contentType) && (
        <div className="flex flex-col gap-3">
          <p className="text-sm text-muted-foreground">
            Preview is not available for this format ({preview.contentType}).
          </p>
          <a
            href={preview.url}
            download={preview.title}
            className="inline-flex min-h-11 cursor-pointer items-center justify-center rounded-md bg-primary px-4 py-2 text-sm font-medium text-on-primary"
          >
            Download {preview.title}
          </a>
        </div>
      )}
    </Modal>
  );
}
