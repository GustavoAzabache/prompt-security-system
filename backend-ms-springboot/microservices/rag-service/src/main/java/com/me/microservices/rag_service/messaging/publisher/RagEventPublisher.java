package com.me.microservices.rag_service.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.me.microservices.rag_service.messaging.config.RabbitPublisherConfig;
import com.me.microservices.rag_service.messaging.events.DocumentStatusChangedEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RagEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    private static final String RAG_DOCUMENT_STATUS_CHANGED_ROUTING_KEY = "rag.event.status-changed";

    public void changeStatus(String documentId, String statusChanged) {
        rabbitTemplate.convertAndSend(
            RabbitPublisherConfig.RAG_EVENT_EXCHANGE_NAME,
            RAG_DOCUMENT_STATUS_CHANGED_ROUTING_KEY,
            new DocumentStatusChangedEvent(documentId, statusChanged)
        );
    }
}
