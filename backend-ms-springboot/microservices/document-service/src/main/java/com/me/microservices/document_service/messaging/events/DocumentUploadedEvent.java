package com.me.microservices.document_service.messaging.events;

public record DocumentUploadedEvent(
    String documentId,
    String filename
) {}
