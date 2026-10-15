package com.me.microservices.rag_service.document.dto;

public record DocumentMetadata(
    String id,
    String title,
    long size,
    String type,
    String area,
    String confidentialityLevel
) {}
