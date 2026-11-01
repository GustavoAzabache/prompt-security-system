package com.me.microservices.document_service.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.me.microservices.document_service.document.service.DocumentService;
import com.me.microservices.document_service.messaging.config.RabbitListenerConfig;
import com.me.microservices.document_service.messaging.events.DocumentStatusChangedEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DocumentStatusChangedListener {

    private final DocumentService documentService;

    @RabbitListener(queues = RabbitListenerConfig.RAG_DOCUMENT_STATUS_CHANGED_QUEUE_NAME)
    public void onMessage(DocumentStatusChangedEvent event) {
        documentService.changeStatus(event);
    }
}
