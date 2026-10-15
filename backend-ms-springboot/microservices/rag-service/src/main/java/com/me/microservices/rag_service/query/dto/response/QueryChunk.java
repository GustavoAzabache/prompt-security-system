package com.me.microservices.rag_service.query.dto.response;

public record QueryChunk(
    String text,
    Double score,
    String documentId,
    String title,
    String area,
    String confidentialityLevel,
    String chunkIndex
) {}
