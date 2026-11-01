package com.me.microservices.document_service.messaging.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitPublisherConfig {

    public static final String REQUEST_EXCHANGE_NAME = "document.exchange.request";
    
    @Bean
    public TopicExchange documentRequestExchange() {
        return new TopicExchange(REQUEST_EXCHANGE_NAME);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}