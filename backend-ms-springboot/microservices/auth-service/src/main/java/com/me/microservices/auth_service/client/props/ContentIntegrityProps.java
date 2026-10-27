package com.me.microservices.auth_service.client.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
@Component
@ConfigurationProperties(prefix = "clients.content-integrity")
public class ContentIntegrityProps {

    private String name;
    private String password;
}
