package com.me.microservices.rag_service.ask.dto.response;

public record AskSource(
    String documentId,
    String title,
    String chunkIndex
) {}
