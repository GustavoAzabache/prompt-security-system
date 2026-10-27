package com.me.microservices.document_service.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitQueuePublisherConfig {

    public static final String EXCHANGE_NAME = "document.exchange";

    public static final String CONFIDENTIALITY_QUEUE_NAME = "document.confidentiality.queue";
    public static final String ROUTING_KEY_CONFIDENTIALITY = "document.event.confidentiality-changed";

    public static final String SCAN_QUEUE_NAME = "document.scan.queue";
    public static final String ROUTING_KEY_UPLOADED = "document.event.uploaded";
    
    @Bean
    public TopicExchange documentExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue documentConfidentialityQueue() {
        return new Queue(CONFIDENTIALITY_QUEUE_NAME, true);
    }

    @Bean
    public Binding bindingDocumentConfidentiality(Queue documentConfidentialityQueue, TopicExchange documentExchange) {
        return BindingBuilder.bind(documentConfidentialityQueue).to(documentExchange).with(ROUTING_KEY_CONFIDENTIALITY);
    }

    @Bean
    public Queue documentScanQueue() {
        return new Queue(SCAN_QUEUE_NAME, true);
    }

    @Bean
    public Binding bindingDocumentUploaded(Queue documentScanQueue, TopicExchange documentExchange) {
        return BindingBuilder.bind(documentScanQueue).to(documentExchange).with(ROUTING_KEY_UPLOADED);
    }
}
