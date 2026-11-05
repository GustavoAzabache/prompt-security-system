import { useState } from 'react';
import axios from 'axios';
import { Button } from '@/components/ui/Button';
import { Modal } from '@/components/ui/Modal';
import { Select } from '@/components/ui/Select';
import { CONFIDENTIALITY_LEVELS } from '@/constants/roles';
import { documentService } from '@/services/documentService';
import type { ErrorResponse } from '@/types/api.types';
import type { ConfidentialityLevel, DocumentListItem } from '@/types/document.types';

interface ChangeConfidentialityModalProps {
  doc: DocumentListItem;
  onClose: () => void;
  /** Refresca la lista para que la fila muestre el nivel nuevo. */
  onChanged: () => void;
}

const LEVELS: readonly string[] = CONFIDENTIALITY_LEVELS;

function initialLevel(doc: DocumentListItem): ConfidentialityLevel {
  return LEVELS.includes(doc.confidentialityLevel)
    ? (doc.confidentialityLevel as ConfidentialityLevel)
    : 'PUBLIC';
}

/** Mensaje real del backend (ForbiddenDocumentUpdateException → messages[]). */
function serverMessage(e: unknown): string | null {
  if (!axios.isAxiosError(e)) return null;
  const data = e.response?.data as Partial<ErrorResponse> | undefined;
  const messages = data && Array.isArray(data.messages) ? data.messages : [];
  return messages.length > 0 ? messages.join(' · ') : null;
}

export function ChangeConfidentialityModal({ doc, onClose, onChanged }: ChangeConfidentialityModalProps) {
  const [level, setLevel] = useState<ConfidentialityLevel>(initialLevel(doc));
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [saved, setSaved] = useState<string | null>(null);

  const save = async () => {
    setSaving(true);
    setError(null);
    setSaved(null);
    try {
      const data = await documentService.changeConfidentiality(doc.id, {
        confidentialityLevel: level,
      });
      setSaved(data.message);
      onChanged();
    } catch (e) {
      setError(
        serverMessage(e) ?? 'No se pudo actualizar la confidencialidad. Intenta de nuevo.',
      );
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal title="Cambiar confidencialidad" onClose={onClose}>
      <div className="flex flex-col gap-4">
        <div className="flex flex-col gap-1">
          <p className="text-sm font-medium text-foreground">Documento</p>
          <p className="text-sm text-muted-foreground">{doc.title}</p>
          <p className="text-sm text-muted-foreground">
            Nivel actual:{' '}
            <span className="font-medium text-foreground">{doc.confidentialityLevel}</span>
          </p>
        </div>

        {saved ? (
          <>
            <p className="text-sm text-accent">{saved}</p>
            <div className="flex justify-end">
              <Button type="button" onClick={onClose}>
                Cerrar
              </Button>
            </div>
          </>
        ) : (
          <>
            <Select
              label="Nuevo nivel"
              value={level}
              disabled={saving}
              onChange={(e) => setLevel(e.target.value as ConfidentialityLevel)}
            >
              {CONFIDENTIALITY_LEVELS.map((l) => (
                <option key={l} value={l}>
                  {l}
                </option>
              ))}
            </Select>

            {error && (
              <p role="alert" className="text-sm text-destructive">
                {error}
              </p>
            )}

            <div className="flex justify-end gap-2">
              <Button type="button" variant="ghost" onClick={onClose} disabled={saving}>
                Cancelar
              </Button>
              <Button type="button" onClick={() => void save()} disabled={saving}>
                {saving ? 'Guardando…' : 'Guardar'}
              </Button>
            </div>
          </>
        )}
      </div>
    </Modal>
  );
}
