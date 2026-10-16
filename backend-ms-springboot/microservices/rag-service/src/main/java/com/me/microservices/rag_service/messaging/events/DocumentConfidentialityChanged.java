package com.me.microservices.rag_service.messaging.events;

public record DocumentConfidentialityChanged(
    String documentId,
    String currentConfidentialityLevel
) {}