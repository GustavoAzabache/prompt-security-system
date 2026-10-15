import axios from 'axios';
import { api } from '@/lib/api';
import type {
  IngestResponse,
  RagAskResponse,
  RagQueryRequest,
  RagQueryResponse,
} from '@/types/rag.types';

const BASE = '/api/v1/rag';
const ASK_TIMEOUT_MS = 90_000;
const RETRY_BACKOFF_MS = 2_000;

function wait(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

export const ragService = {
  ingest: (documentId: string, force = false) =>
    api
      .post<IngestResponse>(`${BASE}/documents/${documentId}/ingest`, null, {
        params: { force },
      })
      .then((r) => r.data),

  query: (dto: RagQueryRequest, signal?: AbortSignal) =>
    api.post<RagQueryResponse>(`${BASE}/query`, dto, { signal }).then((r) => r.data),

  ask: async (dto: RagQueryRequest, signal?: AbortSignal): Promise<RagAskResponse> => {
    const config = { timeout: ASK_TIMEOUT_MS, signal };
    try {
      return await api.post<RagAskResponse>(`${BASE}/ask`, dto, config).then((r) => r.data);
    } catch (e) {
      if (axios.isAxiosError(e) && e.response?.status === 500 && !signal?.aborted) {
        await wait(RETRY_BACKOFF_MS);
        if (signal?.aborted) throw e;
        return await api.post<RagAskResponse>(`${BASE}/ask`, dto, config).then((r) => r.data);
      }
      throw e;
    }
  },
};
