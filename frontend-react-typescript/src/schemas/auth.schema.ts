import { z } from 'zod';

export const loginSchema = z.object({
  userType: z.enum(['employee', 'external']),
  email: z.string().trim().min(1, 'Email is required').email('Email must be valid'),
  password: z.string().min(1, 'Password is required'),
});

export type LoginFormValues = z.infer<typeof loginSchema>;

const personFields = {
  firstName: z.string().trim().min(1, 'First name is required').max(50, 'Max 50 characters'),
  lastName: z.string().trim().min(1, 'Last name is required').max(50, 'Max 50 characters'),
  email: z
    .string()
    .trim()
    .min(1, 'Email is required')
    .email('Email must be valid')
    .max(255, 'Max 255 characters'),
  password: z.string().min(8, 'Min 8 characters').max(50, 'Max 50 characters'),
};

export const registerEmployeeSchema = z.object({
  ...personFields,
  area: z.string().trim().min(1, 'Area is required').max(50, 'Max 50 characters'),
});

export type RegisterEmployeeFormValues = z.infer<typeof registerEmployeeSchema>;

export const registerExternalSchema = z.object({ ...personFields });

export type RegisterExternalFormValues = z.infer<typeof registerExternalSchema>;
