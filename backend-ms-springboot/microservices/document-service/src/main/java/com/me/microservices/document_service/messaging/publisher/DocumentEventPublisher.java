package com.me.microservices.document_service.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.me.microservices.document_service.messaging.events.DocumentConfidentialityChanged;
import com.me.microservices.document_service.messaging.events.DocumentUploaded;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "document.exchange";

    public void confidentialityChanged(String documentId, String currentConfidentialityLevel) {
        rabbitTemplate.convertAndSend(
            EXCHANGE_NAME,
            "document.event.confidentiality-changed",   // routing key = nombre de la cola (exchange default)
            new DocumentConfidentialityChanged(documentId, currentConfidentialityLevel));
    }

    // Uso de los datos del evento:
    // documentId: al momento de que el consumer vaya a getContent necesitará el id
    public void documentUploaded(String documentId, String filename) {
        rabbitTemplate.convertAndSend(
            EXCHANGE_NAME,
            "document.event.uploaded",
            new DocumentUploaded(documentId, filename)
        );
    }
}
