package com.me.microservices.rag_service.messaging.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitPublisherConfig {

    // Exchange para el evento: rag-service -> document-service
    public static final String RAG_EVENT_EXCHANGE_NAME = "rag.exchange.event";
    
    @Bean
    public TopicExchange ragEventExchange() {
        return new TopicExchange(RAG_EVENT_EXCHANGE_NAME);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}