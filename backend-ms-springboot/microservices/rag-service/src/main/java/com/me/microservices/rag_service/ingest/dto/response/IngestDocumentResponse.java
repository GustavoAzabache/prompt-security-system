package com.me.microservices.rag_service.ingest.dto.response;

import com.me.microservices.rag_service.document.dto.DocumentMetadata;

public record IngestDocumentResponse(
    String documentId,
    String status,
    DocumentMetadata metadata,
    Integer textLength,
    Integer chunkCount
) {}
