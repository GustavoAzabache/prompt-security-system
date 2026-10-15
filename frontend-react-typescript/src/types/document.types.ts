export type TypeDocument = 'PDF' | 'DOCX' | 'XLSX' | 'TXT';

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
}

export interface DocumentPreview {
  url: string;
  contentType: string;
  title: string;
}
