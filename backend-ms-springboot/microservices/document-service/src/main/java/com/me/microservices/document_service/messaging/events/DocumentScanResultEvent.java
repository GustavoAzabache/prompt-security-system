package com.me.microservices.document_service.messaging.events;

import java.util.List;

public record DocumentScanResultEvent(
    String documentId,
    boolean accept,
    List<String> findings
) {}