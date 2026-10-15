package com.me.microservices.rag_service.query.dto.request;

public record QueryRequest(
    String query,
    Integer topK
) {}
