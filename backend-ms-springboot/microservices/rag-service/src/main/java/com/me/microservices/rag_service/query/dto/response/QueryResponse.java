package com.me.microservices.rag_service.query.dto.response;

import java.util.List;

public record QueryResponse(
    String query,
    int topK,
    int resultCount,
    List<QueryChunk> results
) {}
