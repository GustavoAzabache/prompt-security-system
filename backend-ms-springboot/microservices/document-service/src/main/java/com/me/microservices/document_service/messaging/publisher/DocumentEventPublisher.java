package com.me.microservices.document_service.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.me.microservices.document_service.messaging.config.RabbitPublisherConfig;
import com.me.microservices.document_service.messaging.events.DocumentUploadedEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    private static final String DOCUMENT_UPLOADED_ROUTING_KEY = "document.event.upload";

    public void upload(String documentId, String filename) {
        rabbitTemplate.convertAndSend(
            RabbitPublisherConfig.REQUEST_EXCHANGE_NAME,
            DOCUMENT_UPLOADED_ROUTING_KEY,
            new DocumentUploadedEvent(documentId, filename)
        );
    }
}
