import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { FormError } from '@/components/ui/FormError';
import { Input } from '@/components/ui/Input';
import { useAuth } from '@/hooks/useAuth';
import { USER_TYPE_LABELS } from '@/constants/labels';
import { PATHS } from '@/routes/paths';
import {
  registerEmployeeSchema,
  registerExternalSchema,
  type RegisterEmployeeFormValues,
  type RegisterExternalFormValues,
} from '@/schemas/auth.schema';
import type { UserType } from '@/types/auth.types';

export function RegisterPage() {
  const { registerEmployee, registerExternal } = useAuth();
  const navigate = useNavigate();
  const [userType, setUserType] = useState<UserType>('employee');
  const [submitError, setSubmitError] = useState<string | null>(null);

  const employeeForm = useForm<RegisterEmployeeFormValues>({
    resolver: zodResolver(registerEmployeeSchema),
    defaultValues: { firstName: '', lastName: '', area: '', email: '', password: '' },
  });
  const externalForm = useForm<RegisterExternalFormValues>({
    resolver: zodResolver(registerExternalSchema),
    defaultValues: { firstName: '', lastName: '', email: '', password: '' },
  });

  const onSubmitEmployee = employeeForm.handleSubmit(async (values) => {
    setSubmitError(null);
    try {
      await registerEmployee(values);
      navigate(PATHS.documents);
    } catch {
      setSubmitError('No se pudo registrar. El correo puede estar en uso.');
    }
  });

  const onSubmitExternal = externalForm.handleSubmit(async (values) => {
    setSubmitError(null);
    try {
      await registerExternal(values);
      navigate(PATHS.home);
    } catch {
      setSubmitError('No se pudo registrar. El correo puede estar en uso.');
    }
  });

  return (
    <section className="mx-auto flex w-full max-w-md flex-col gap-4">
      <h1 className="text-2xl font-semibold">Registrarse</h1>
      <Card className="p-6">
        <div className="mb-4 flex flex-col gap-1.5">
          <label htmlFor="userType" className="text-sm font-medium text-foreground">
            Tipo de usuario
          </label>
          <select
            id="userType"
            value={userType}
            onChange={(e) => {
              setUserType(e.target.value as UserType);
              setSubmitError(null);
            }}
            className="min-h-11 rounded-md border border-border bg-card px-3 py-2 text-base"
          >
            <option value="employee">{USER_TYPE_LABELS.employee}</option>
            <option value="external">{USER_TYPE_LABELS.external}</option>
          </select>
        </div>
        {userType === 'employee' ? (
          <form onSubmit={onSubmitEmployee} className="flex flex-col gap-4" noValidate>
            <FormError message={submitError} />
            <Input label="Nombre" autoComplete="given-name" error={employeeForm.formState.errors.firstName?.message} {...employeeForm.register('firstName')} />
            <Input label="Apellido" autoComplete="family-name" error={employeeForm.formState.errors.lastName?.message} {...employeeForm.register('lastName')} />
            <Input label="Área" placeholder="p. ej. Finanzas" error={employeeForm.formState.errors.area?.message} {...employeeForm.register('area')} />
            <Input label="Correo" type="email" autoComplete="email" error={employeeForm.formState.errors.email?.message} {...employeeForm.register('email')} />
            <Input label="Contraseña" type="password" autoComplete="new-password" error={employeeForm.formState.errors.password?.message} {...employeeForm.register('password')} />
            <Button type="submit" disabled={employeeForm.formState.isSubmitting}>
              {employeeForm.formState.isSubmitting ? 'Creando…' : 'Registrar empleado'}
            </Button>
          </form>
        ) : (
          <form onSubmit={onSubmitExternal} className="flex flex-col gap-4" noValidate>
            <FormError message={submitError} />
            <Input label="Nombre" autoComplete="given-name" error={externalForm.formState.errors.firstName?.message} {...externalForm.register('firstName')} />
            <Input label="Apellido" autoComplete="family-name" error={externalForm.formState.errors.lastName?.message} {...externalForm.register('lastName')} />
            <Input label="Correo" type="email" autoComplete="email" error={externalForm.formState.errors.email?.message} {...externalForm.register('email')} />
            <Input label="Contraseña" type="password" autoComplete="new-password" error={externalForm.formState.errors.password?.message} {...externalForm.register('password')} />
            <Button type="submit" disabled={externalForm.formState.isSubmitting}>
              {externalForm.formState.isSubmitting ? 'Creando…' : 'Registrar externo'}
            </Button>
          </form>
        )}
      </Card>
    </section>
  );
}
