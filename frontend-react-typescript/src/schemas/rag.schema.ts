import { z } from 'zod';

export const ragQuerySchema = z.object({
  query: z.string().trim().min(1, 'Question is required').max(2000, 'Max 2000 characters'),
  topK: z.coerce.number().int().min(1).max(10),
});

export type RagQueryFormValues = z.infer<typeof ragQuerySchema>;
export type RagQueryFormInput = z.input<typeof ragQuerySchema>;
