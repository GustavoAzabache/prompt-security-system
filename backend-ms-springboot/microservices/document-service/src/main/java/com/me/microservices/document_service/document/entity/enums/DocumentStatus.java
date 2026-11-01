package com.me.microservices.document_service.document.entity.enums;

public enum DocumentStatus {
    PENDING,
    CONTENT_READY_TO_READ,
    CONTENT_REJECTED,
    INDEXING,
    READY_TO_READ_AND_CONSULT,
}