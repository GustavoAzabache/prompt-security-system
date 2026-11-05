import { CONFIDENTIALITY_LEVELS } from './roles';

/**
 * Etiquetas en español para enums. Los `value` que viajan al backend
 * siguen siendo los valores crudos; esto es solo presentación.
 */

export const CONFIDENTIALITY_LABELS: Record<(typeof CONFIDENTIALITY_LEVELS)[number], string> = {
  PUBLIC: 'Público',
  PRIVATE_ALL_AREAS: 'Privado (todas las áreas)',
  PRIVATE_ONLY_AREA: 'Privado (solo mi área)',
  CONFIDENTIAL: 'Confidencial',
};

export const USER_TYPE_LABELS: Record<'employee' | 'external', string> = {
  employee: 'Empleado',
  external: 'Externo',
};
