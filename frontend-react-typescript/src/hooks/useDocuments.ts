import { useCallback, useState } from 'react';
import { documentService } from '@/services/documentService';
import type {
  ChangeConfidentialityRequest,
  DocumentListItem,
  UploadDocumentResponse,
} from '@/types/document.types';

export function useDocuments() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [lastUpload, setLastUpload] = useState<UploadDocumentResponse | null>(null);
  const [lastMessage, setLastMessage] = useState<string | null>(null);
  const [documents, setDocuments] = useState<DocumentListItem[]>([]);
  const [listLoading, setListLoading] = useState(false);
  const [listError, setListError] = useState<string | null>(null);

  const refreshList = useCallback(async () => {
    setListLoading(true);
    setListError(null);
    try {
      const data = await documentService.list();
      setDocuments(data);
      return data;
    } catch {
      setListError('Could not load documents');
      throw new Error('Could not load documents');
    } finally {
      setListLoading(false);
    }
  }, []);

  const upload = useCallback(
    async (file: File) => {
      setLoading(true);
      setError(null);
      try {
        const data = await documentService.upload(file);
        setLastUpload(data);
        await refreshList().catch(() => {});
        return data;
      } catch (e) {
        setError('Upload failed');
        throw e;
      } finally {
        setLoading(false);
      }
    },
    [refreshList],
  );

  const changeConfidentiality = useCallback(async (id: string, dto: ChangeConfidentialityRequest) => {
    setLoading(true);
    setError(null);
    try {
      const data = await documentService.changeConfidentiality(id, dto);
      setLastMessage(data.message);
      return data;
    } catch (e) {
      setError('Update failed');
      throw e;
    } finally {
      setLoading(false);
    }
  }, []);

  return {
    loading,
    error,
    lastUpload,
    lastMessage,
    upload,
    changeConfidentiality,
    documents,
    listLoading,
    listError,
    refreshList,
  };
}
