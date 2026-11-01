package com.me.microservices.document_service.messaging.events;

public record DocumentStatusChangedEvent(
    String documentId,
    String statusChanged
) {}
