package com.me.microservices.rag_service.messaging.listener;

import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.me.microservices.rag_service.ingest.service.IngestService;
import com.me.microservices.rag_service.messaging.events.DocumentConfidentialityChanged;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ConfidentialityChangedListener {

    private final IngestService ingestService;

    @RabbitListener(queues = "document.confidentiality-changed")
    public void onMessage(DocumentConfidentialityChanged event) {
        ingestService.updateConfidentiality(UUID.fromString(event.documentId()), event.currentConfidentialityLevel());
    }
}
