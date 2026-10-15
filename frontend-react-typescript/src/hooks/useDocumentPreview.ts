import { useCallback, useEffect, useRef, useState } from 'react';
import axios from 'axios';
import { documentService } from '@/services/documentService';
import type { ErrorResponse } from '@/types/api.types';
import type { DocumentPreview } from '@/types/document.types';

export type PreviewStatus = 'idle' | 'loading' | 'allowed' | 'denied';

export function useDocumentPreview() {
  const [status, setStatus] = useState<PreviewStatus>('idle');
  const [preview, setPreview] = useState<DocumentPreview | null>(null);
  const [reasons, setReasons] = useState<string[]>([]);
  const urlRef = useRef<string | null>(null);

  const close = useCallback(() => {
    if (urlRef.current) {
      URL.revokeObjectURL(urlRef.current);
      urlRef.current = null;
    }
    setPreview(null);
    setReasons([]);
    setStatus('idle');
  }, []);

  useEffect(
    () => () => {
      if (urlRef.current) URL.revokeObjectURL(urlRef.current);
    },
    [],
  );

  const open = useCallback(async (doc: { id: string; title: string }) => {
    close();
    setStatus('loading');
    try {
      const { blob, contentType } = await documentService.getContent(doc.id);
      const url = URL.createObjectURL(blob);
      urlRef.current = url;
      setPreview({ url, contentType, title: doc.title });
      setStatus('allowed');
    } catch (e) {
      setReasons(await extractMessages(e));
      setStatus('denied');
    }
  }, [close]);

  return { status, preview, reasons, open, close };
}

async function extractMessages(e: unknown): Promise<string[]> {
  if (axios.isAxiosError(e)) {
    const data = e.response?.data;
    if (data !== undefined && data !== null) {
      try {
        const raw = data instanceof Blob ? await data.text() : JSON.stringify(data);
        const parsed = JSON.parse(raw) as Partial<ErrorResponse>;
        if (Array.isArray(parsed.messages) && parsed.messages.length > 0) {
          return parsed.messages;
        }
      } catch {
        // fall through to generic message
      }
    }
    if (e.response?.status === 404) return ['Document not found'];
    if (e.response?.status === 403) return ['Access denied by the server'];
  }
  return ['Could not open the document'];
}
