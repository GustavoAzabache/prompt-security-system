package com.me.microservices.document_service.messaging.events;

public record DocumentConfidentialityChanged(
    String documentId,
    String currentConfidentialityLevel
) {}
