package com.me.microservices.document_service.storage.dto;

import java.util.UUID;

public record StoredFile(
    UUID documentId,
    String originalFilename,
    long size,
    String location
) {}
