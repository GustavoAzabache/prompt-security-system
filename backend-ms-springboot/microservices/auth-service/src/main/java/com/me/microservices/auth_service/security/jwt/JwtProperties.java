package com.me.microservices.auth_service.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private Resource privateKey;
    private Resource publicKey;
    private long expirationAccess;
    private long expirationRefresh;
    private String issuer;
}
