package com.me.microservices.document_service.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitQueueListenerConfig {

    public static final String EXCHANGE_NAME = "document.events.exchange";

    public static final String SCAN_RESULT_QUEUE_NAME = "document.scan.result.queue";
    public static final String ROUTING_KEY_SCAN_RESULT = "DOCUMENT_SCAN_RESULT";

    @Bean
    public DirectExchange documentEventsExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue documentScanResultQueue() {
        return new Queue(SCAN_RESULT_QUEUE_NAME, true);
    }

    @Bean
    public Binding bindingDocumentScanResult(
            Queue documentScanResultQueue,
            DirectExchange documentEventsExchange
    ) {
        return BindingBuilder
                .bind(documentScanResultQueue)
                .to(documentEventsExchange)
                .with(ROUTING_KEY_SCAN_RESULT);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}