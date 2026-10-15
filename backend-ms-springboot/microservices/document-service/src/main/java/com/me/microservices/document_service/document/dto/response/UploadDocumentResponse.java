package com.me.microservices.document_service.document.dto.response;

import com.me.microservices.document_service.document.entity.enums.TypeDocument;

public record UploadDocumentResponse(
    String id,
    String name,
    TypeDocument type,
    long size
) {}
