import { z } from 'zod';

export const promoteSchema = z.object({
  id: z.string().trim().uuid('Must be a valid UUID'),
});

export type PromoteFormValues = z.infer<typeof promoteSchema>;
