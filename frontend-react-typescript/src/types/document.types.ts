export type TypeDocument = 'PDF' | 'DOCX' | 'XLSX' | 'TXT';

export type DocumentStatus =
  | 'PENDING'
  | 'CONTENT_READY_TO_READ'
  | 'CONTENT_REJECTED'
  | 'INDEXING'
  | 'READY_TO_READ_AND_CONSULT';

export type ConfidentialityLevel =
  | 'PUBLIC'
  | 'PRIVATE_ALL_AREAS'
  | 'PRIVATE_ONLY_AREA'
  | 'CONFIDENTIAL';

export interface ChangeConfidentialityRequest {
  confidentialityLevel: ConfidentialityLevel;
}

export interface UploadDocumentResponse {
  id: string;
  name: string;
  type: TypeDocument;
  size: number;
}

export interface ChangeConfidentialityResponse {
  message: string;
}

export interface DocumentListItem {
  id: string;
  title: string;
  size: number;
  type: string;
  area: string;
  confidentialityLevel: string;
  status: DocumentStatus;
  /** Motivos de rechazo; solo presente (no vacío) si status es CONTENT_REJECTED. */
  findings?: string[];
}

export interface DocumentPreview {
  url: string;
  contentType: string;
  title: string;
}
