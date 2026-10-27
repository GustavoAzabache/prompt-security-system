package com.me.microservices.document_service.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.me.microservices.document_service.document.service.DocumentService;
import com.me.microservices.document_service.messaging.events.DocumentScanResult;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DocumentScanResultListener {

    private final DocumentService documentService;

    @RabbitListener(queues = "document.scan.result.queue")
    public void onMessage(DocumentScanResult event) {
        if (event.accept()) {
            documentService.acceptDocument(event.documentId(), event.filename());
        } else {
            documentService.rejectDocument(event.documentId(), event.filename(), event.findings());
        }
    }
}
