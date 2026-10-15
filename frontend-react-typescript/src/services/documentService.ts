import { api } from '@/lib/api';
import type {
  ChangeConfidentialityRequest,
  ChangeConfidentialityResponse,
  DocumentListItem,
  UploadDocumentResponse,
} from '@/types/document.types';

const BASE = '/api/v1/documents';

export const documentService = {
  list: () => api.get<DocumentListItem[]>(BASE).then((r) => r.data),
  getContent: (id: string) =>
    api.get<Blob>(`${BASE}/${id}/content`, { responseType: 'blob' }).then((r) => ({
      blob: r.data,
      contentType:
        (r.headers['content-type'] as string | undefined) ?? 'application/octet-stream',
    })),
  upload: (file: File) => {
    const form = new FormData();
    form.append('file', file);
    return api.post<UploadDocumentResponse>(BASE, form).then((r) => r.data);
  },
  changeConfidentiality: (id: string, dto: ChangeConfidentialityRequest) =>
    api
      .patch<ChangeConfidentialityResponse>(`${BASE}/${id}/change-confidentiality`, dto)
      .then((r) => r.data),
};
