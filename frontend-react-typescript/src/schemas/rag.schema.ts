import { z } from 'zod';

export const ragQuerySchema = z.object({
  query: z.string().trim().min(1, 'La pregunta es obligatoria').max(2000, 'Máximo 2000 caracteres'),
  topK: z.coerce.number().int().min(1).max(10),
});

export type RagQueryFormValues = z.infer<typeof ragQuerySchema>;
export type RagQueryFormInput = z.input<typeof ragQuerySchema>;
