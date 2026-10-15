export const STORAGE_KEYS = {
  accessToken: 'pss.accessToken',
  refreshToken: 'pss.refreshToken',
  userType: 'pss.userType',
} as const;

export const HIERARCHY_LEVELS = ['OPERATIVE', 'JEFE', 'GERENTE', 'DIRECTOR'] as const;

export const CONFIDENTIALITY_LEVELS = [
  'PUBLIC',
  'PRIVATE_ALL_AREAS',
  'PRIVATE_ONLY_AREA',
  'CONFIDENTIAL',
] as const;

export const TYPE_DOCUMENTS = ['PDF', 'DOCX', 'XLSX', 'TXT'] as const;

export const DOCUMENTS_PAGE_SIZE = 10;
