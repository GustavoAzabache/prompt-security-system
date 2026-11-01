package com.me.microservices.document_service.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitListenerConfig {

    public static final String RESPONSE_EXCHANGE_NAME = "document.exchange.response";

    public static final String DOCUMENT_SCAN_RESULT_QUEUE_NAME = "document.queue.scan-result";
    public static final String DOCUMENT_SCAN_RESULT_ROUTING_KEY = "document.event.scan-result";

    @Bean
    public TopicExchange documentResponseExchange() {
        return new TopicExchange(RESPONSE_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue documentScanResultQueue() {
        return new Queue(DOCUMENT_SCAN_RESULT_QUEUE_NAME, true);
    }

    @Bean
    public Binding bindingDocumentScanResult(
            Queue documentScanResultQueue,
            TopicExchange documentResponseExchange
    ) {
        return BindingBuilder
                .bind(documentScanResultQueue)
                .to(documentResponseExchange)
                .with(DOCUMENT_SCAN_RESULT_ROUTING_KEY);
    }
}