package com.me.microservices.rag_service.ask.dto.request;

public record AskRequest(
    String query,
    Integer topK
) {}
