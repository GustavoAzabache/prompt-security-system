import { z } from 'zod';
import { CONFIDENTIALITY_LEVELS } from '@/constants/roles';

export const changeConfidentialitySchema = z.object({
  id: z.string().trim().min(1, 'Document ID is required'),
  confidentialityLevel: z.enum(CONFIDENTIALITY_LEVELS),
});

export type ChangeConfidentialityFormValues = z.infer<typeof changeConfidentialitySchema>;

const MAX_FILE_MB = 10;

export const uploadDocumentSchema = z.object({
  file: z
    .instanceof(File, { message: 'File is required' })
    .refine((f) => f.size > 0, 'Empty file')
    .refine(
      (f) => f.size <= MAX_FILE_MB * 1024 * 1024,
      `Max ${MAX_FILE_MB}MB`,
    ),
});

export type UploadDocumentFormValues = z.infer<typeof uploadDocumentSchema>;
