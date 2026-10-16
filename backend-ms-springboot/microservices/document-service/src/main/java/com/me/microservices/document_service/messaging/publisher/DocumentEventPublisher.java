package com.me.microservices.document_service.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.me.microservices.document_service.messaging.events.DocumentConfidentialityChanged;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void confidentialityChanged(String documentId, String currentConfidentialityLevel) {
        rabbitTemplate.convertAndSend(
            "document.confidentiality-changed",   // routing key = nombre de la cola (exchange default)
            new DocumentConfidentialityChanged(documentId, currentConfidentialityLevel));
    }
}
