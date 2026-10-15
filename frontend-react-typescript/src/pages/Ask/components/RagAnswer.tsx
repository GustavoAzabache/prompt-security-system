import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import type { RagAskResponse } from '@/types/rag.types';

interface RagAnswerProps {
  answer: RagAskResponse;
  onViewSource: (documentId: string, title: string) => void;
}

export function RagAnswer({ answer, onViewSource }: RagAnswerProps) {
  return (
    <Card className="flex flex-col gap-4 p-6">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <h2 className="text-lg font-semibold">Answer</h2>
        <span className="inline-flex min-h-8 items-center rounded-full bg-muted px-3 py-1 text-xs font-medium text-muted-foreground">
          Modelo: {answer.model ?? 'Sin modelo'}
        </span>
      </div>
      <p className="whitespace-pre-wrap text-base leading-6 text-foreground">{answer.answer}</p>
      {!answer.answered && (
        <p className="text-sm text-muted-foreground">
          No coverage for this question — the answer above is the fixed fallback text.
        </p>
      )}
      {answer.sources.length > 0 && (
        <div className="flex flex-col gap-2">
          <h3 className="text-base font-semibold">Sources</h3>
          <ol className="flex flex-col gap-2">
            {answer.sources.map((s, i) => (
              <li
                key={`${s.documentId}-${s.chunkIndex}`}
                className="flex flex-wrap items-center gap-2 rounded-md border border-border bg-muted/50 px-3 py-2"
              >
                <span
                  aria-label={`Citation ${i + 1}`}
                  className="inline-flex min-h-6 min-w-6 items-center justify-center rounded-full bg-primary px-1 text-xs font-semibold text-on-primary"
                >
                  {i + 1}
                </span>
                <span className="mr-auto text-sm font-medium">{s.title}</span>
                <Button
                  type="button"
                  variant="ghost"
                  onClick={() => onViewSource(s.documentId, s.title)}
                >
                  View
                </Button>
              </li>
            ))}
          </ol>
        </div>
      )}
    </Card>
  );
}
