package com.me.microservices.rag_service.messaging.events;

public record DocumentStatusChangedEvent(
    String documentId,
    String statusChanged
) {}
