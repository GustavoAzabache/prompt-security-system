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

        // Claims de empleados (acceso con rol)
        if (jwt.hasClaim("area") && jwt.getClaimAsString("area") != null) {
            authorities.add(
                new SimpleGrantedAuthority("ROLE_EMPLOYEE")
            );
        }

        // Claim de scope para tokens M2M (client_credentials)
        // convierte "documents:scan" -> "SCOPE_documents:scan"
        String scope = jwt.getClaimAsString("scope");
        if (scope != null && !scope.isBlank()) {
            for (String s : scope.split(" ")) {
                authorities.add(new SimpleGrantedAuthority("SCOPE_" + s));
            }
        }

        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }

}
