export type IngestStatus = 'INDEXED' | 'ALREADY_INDEXED';

export interface RagDocumentMetadata {
  id: string;
  title: string;
  size: number;
  type: string;
  area: string;
  confidentialityLevel: string;
}

export interface IngestResponse {
  documentId: string;
  status: IngestStatus;
  metadata: RagDocumentMetadata;
  textLength: number;
  chunkCount: number;
}

export interface RagQueryRequest {
  query: string;
  topK?: number;
}

export interface RagResult {
  text: string;
  score: number;
  documentId: string;
  title: string;
  area: string;
  confidentialityLevel: string;
  chunkIndex: number;
}

export interface RagQueryResponse {
  query: string;
  topK: number;
  resultCount: number;
  results: RagResult[];
}

export interface RagSource {
  documentId: string;
  title: string;
  chunkIndex: number;
}

export interface RagAskResponse {
  query: string;
  answer: string;
  answered: boolean;
  model: string | null;
  sources: RagSource[];
}
