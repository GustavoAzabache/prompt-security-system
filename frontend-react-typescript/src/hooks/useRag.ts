import { useCallback, useEffect, useRef, useState } from 'react';
import axios from 'axios';
import { ragService } from '@/services/ragService';
import type { RagAskResponse, RagQueryRequest } from '@/types/rag.types';

export type RagAskStatus = 'idle' | 'asking' | 'success' | 'error';

export function useRag() {
  const [askStatus, setAskStatus] = useState<RagAskStatus>('idle');
  const [answer, setAnswer] = useState<RagAskResponse | null>(null);
  const [askError, setAskError] = useState<string | null>(null);
  const [ingesting, setIngesting] = useState(false);
  const abortRef = useRef<AbortController | null>(null);

  const cancelAsk = useCallback(() => {
    abortRef.current?.abort();
  }, []);

  useEffect(
    () => () => {
      abortRef.current?.abort();
    },
    [],
  );

  const ask = useCallback(async (dto: RagQueryRequest) => {
    abortRef.current?.abort();
    const controller = new AbortController();
    abortRef.current = controller;
    setAskStatus('asking');
    setAskError(null);
    try {
      const data = await ragService.ask(dto, controller.signal);
      setAnswer(data);
      setAskStatus('success');
      return data;
    } catch (e) {
      if (axios.isCancel(e) || controller.signal.aborted) {
        setAskStatus('idle');
        return null;
      }
      setAskError('The question failed. Check coverage or try again.');
      setAskStatus('error');
      throw e;
    }
  }, []);

  const ingest = useCallback(async (documentId: string, force = false) => {
    setIngesting(true);
    try {
      return await ragService.ingest(documentId, force);
    } finally {
      setIngesting(false);
    }
  }, []);

  return { askStatus, answer, askError, ask, cancelAsk, ingest, ingesting };
}
