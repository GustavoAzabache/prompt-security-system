package com.me.microservices.document_service.messaging.events;

import java.util.List;

public record DocumentScanResult(
    String documentId,
    String filename,
    boolean accept,
    List<String> findings
) {}