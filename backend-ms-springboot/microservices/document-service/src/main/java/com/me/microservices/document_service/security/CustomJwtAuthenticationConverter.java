package com.me.microservices.document_service.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        if (jwt.hasClaim("area") && jwt.getClaimAsString("area") != null) {
            authorities.add(
                new SimpleGrantedAuthority("ROLE_EMPLOYEE")
            );
        }
        
        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }

}
