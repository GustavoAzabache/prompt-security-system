import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { FormError } from '@/components/ui/FormError';
import { Input } from '@/components/ui/Input';
import { Select } from '@/components/ui/Select';
import { useAuth } from '@/hooks/useAuth';
import { USER_TYPE_LABELS } from '@/constants/labels';
import { PATHS } from '@/routes/paths';
import { loginSchema, type LoginFormValues } from '@/schemas/auth.schema';

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [submitError, setSubmitError] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: { userType: 'employee', email: '', password: '' },
  });

  const onSubmit = handleSubmit(async (values) => {
    setSubmitError(null);
    try {
      await login(values.userType, { email: values.email, password: values.password });
      navigate(values.userType === 'external' ? PATHS.home : PATHS.documents);
    } catch {
      setSubmitError('No se pudo iniciar sesión. Revisa tus credenciales.');
    }
  });

  return (
    <section className="mx-auto flex w-full max-w-md flex-col gap-4">
      <h1 className="text-2xl font-semibold">Iniciar sesión</h1>
      <Card className="p-6">
        <form onSubmit={onSubmit} className="flex flex-col gap-4" noValidate>
          <FormError message={submitError} />
          <Select label="Tipo de usuario" error={errors.userType?.message} {...register('userType')}>
            <option value="employee">{USER_TYPE_LABELS.employee}</option>
            <option value="external">{USER_TYPE_LABELS.external}</option>
          </Select>
          <Input
            label="Correo"
            type="email"
            autoComplete="email"
            placeholder="nombre@empresa.com"
            error={errors.email?.message}
            {...register('email')}
          />
          <Input
            label="Contraseña"
            type="password"
            autoComplete="current-password"
            error={errors.password?.message}
            {...register('password')}
          />
          <Button type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Iniciando sesión…' : 'Iniciar sesión'}
          </Button>
        </form>
      </Card>
    </section>
  );
}
