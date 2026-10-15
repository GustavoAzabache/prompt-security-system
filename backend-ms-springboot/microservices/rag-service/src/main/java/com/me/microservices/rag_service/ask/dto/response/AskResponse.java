package com.me.microservices.rag_service.ask.dto.response;

import java.util.List;

public record AskResponse(
    String query,
    String answer,
    boolean answered,
    List<AskSource> sources
) {}
