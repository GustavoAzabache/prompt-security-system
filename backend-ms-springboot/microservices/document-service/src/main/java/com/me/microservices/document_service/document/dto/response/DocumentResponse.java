package com.me.microservices.document_service.document.dto.response;

import java.util.List;

public record DocumentResponse(
    String id,
    String title,
    long size,
    String type,
    String area,
    String confidentialityLevel,
    String status,
    List<String> findings
) {}
