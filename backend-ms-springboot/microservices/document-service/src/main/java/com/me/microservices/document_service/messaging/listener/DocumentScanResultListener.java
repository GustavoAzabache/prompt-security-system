package com.me.microservices.document_service.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.me.microservices.document_service.document.service.DocumentService;
import com.me.microservices.document_service.messaging.config.RabbitListenerConfig;
import com.me.microservices.document_service.messaging.events.DocumentScanResultEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DocumentScanResultListener {

    private final DocumentService documentService;

    @RabbitListener(queues = RabbitListenerConfig.DOCUMENT_SCAN_RESULT_QUEUE_NAME)
    public void onMessage(DocumentScanResultEvent event) {
        documentService.scanResult(event);
    }
}
