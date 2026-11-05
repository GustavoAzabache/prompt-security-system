import { useCallback, useRef, useState } from 'react';
import { DOCUMENTS_PAGE_SIZE } from '@/constants/roles';
import { documentService } from '@/services/documentService';
import { ragService } from '@/services/ragService';
import type { DocumentListItem } from '@/types/document.types';

export interface IndexResult {
  status: 'INDEXED' | 'ALREADY_INDEXED';
  chunkCount: number | null;
}

export function usePendingIndex() {
  const [documents, setDocuments] = useState<DocumentListItem[]>([]);
  const [listLoading, setListLoading] = useState(false);
  const [listError, setListError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [fetchedCount, setFetchedCount] = useState(0);
  const [indexingId, setIndexingId] = useState<string | null>(null);
  const [indexError, setIndexError] = useState<string | null>(null);
  const pageRef = useRef(0);

  const loadPage = useCallback(async (pageToLoad: number) => {
    pageRef.current = pageToLoad;
    setPage(pageToLoad);
    setListLoading(true);
    setListError(null);
    try {
      const data = await documentService.listNotIndexed(pageToLoad);
      setDocuments(data);
      setFetchedCount(data.length);
      return data;
    } catch {
      setListError('No se pudieron cargar los documentos');
      throw new Error('No se pudieron cargar los documentos');
    } finally {
      setListLoading(false);
    }
  }, []);

  const refreshList = useCallback(() => loadPage(pageRef.current), [loadPage]);

  const indexDocument = useCallback(
    async (id: string): Promise<IndexResult> => {
      setIndexingId(id);
      setIndexError(null);
      try {
        const data = await ragService.ingest(id, false);
        // El documento salió de la lista: quitarlo de la vista local
        setDocuments((prev) => prev.filter((doc) => doc.id !== id));
        return { status: data.status, chunkCount: data.chunkCount };
      } catch {
        // Rollback en backend: el documento sigue en CONTENT_READY_TO_READ
        setIndexError('La indexación falló. El documento permanece pendiente.');
        throw new Error('La indexación falló');
      } finally {
        setIndexingId(null);
      }
    },
    [],
  );

  return {
    documents,
    listLoading,
    listError,
    page,
    hasMore: fetchedCount === DOCUMENTS_PAGE_SIZE,
    indexingId,
    indexError,
    loadPage,
    refreshList,
    indexDocument,
  };
}
