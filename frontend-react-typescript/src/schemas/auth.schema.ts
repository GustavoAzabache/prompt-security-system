import { z } from 'zod';

export const loginSchema = z.object({
  userType: z.enum(['employee', 'external']),
  email: z.string().trim().min(1, 'El correo es obligatorio').email('El correo no es válido'),
  password: z.string().min(1, 'La contraseña es obligatoria'),
});

export type LoginFormValues = z.infer<typeof loginSchema>;

const personFields = {
  firstName: z.string().trim().min(1, 'El nombre es obligatorio').max(50, 'Máximo 50 caracteres'),
  lastName: z.string().trim().min(1, 'El apellido es obligatorio').max(50, 'Máximo 50 caracteres'),
  email: z
    .string()
    .trim()
    .min(1, 'El correo es obligatorio')
    .email('El correo no es válido')
    .max(255, 'Máximo 255 caracteres'),
  password: z.string().min(8, 'Mínimo 8 caracteres').max(50, 'Máximo 50 caracteres'),
};

export const registerEmployeeSchema = z.object({
  ...personFields,
  area: z.string().trim().min(1, 'El área es obligatoria').max(50, 'Máximo 50 caracteres'),
});

export type RegisterEmployeeFormValues = z.infer<typeof registerEmployeeSchema>;

export const registerExternalSchema = z.object({ ...personFields });

export type RegisterExternalFormValues = z.infer<typeof registerExternalSchema>;
