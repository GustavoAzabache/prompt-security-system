package com.me.microservices.document_service.messaging.events;

public record DocumentUploaded(
    String documentId,
    String filename
) {}
