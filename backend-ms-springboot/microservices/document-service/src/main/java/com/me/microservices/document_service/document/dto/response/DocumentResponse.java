package com.me.microservices.document_service.document.dto.response;

public record DocumentResponse(
    String id,
    String title,
    long size,
    String type,
    String area,
    String confidentialityLevel
) {}
