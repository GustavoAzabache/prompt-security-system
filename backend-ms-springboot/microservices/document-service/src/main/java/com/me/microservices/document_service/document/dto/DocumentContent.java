package com.me.microservices.document_service.document.dto;

import org.springframework.core.io.Resource;

public record DocumentContent(
    String mediaType,
    long contentLength,
    String fileName,
    Resource resource
) {}
