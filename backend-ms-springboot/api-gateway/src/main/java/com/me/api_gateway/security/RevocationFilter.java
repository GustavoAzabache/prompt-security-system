package com.me.api_gateway.security;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class RevocationFilter implements WebFilter {

    private static final String REVOKED_KEY_PREFIX = "revoked:";

    private final ReactiveStringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .filter(JwtAuthenticationToken.class::isInstance)
                .cast(JwtAuthenticationToken.class)
                .map(jwtAuthentication -> jwtAuthentication.getToken().getId())
                .filter(jti -> jti != null)
                .flatMap(jti -> redisTemplate.hasKey(REVOKED_KEY_PREFIX + jti))
                .onErrorResume(exception -> {
                    log.warn("No se pudo verificar revocacion en Redis, se permite el request: {}", exception.getMessage());
                    return Mono.just(false);
                })
                .defaultIfEmpty(false)
                .flatMap(revoked -> Boolean.TRUE.equals(revoked)
                        ? unauthorized(exchange)
                        : chain.filter(exchange));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = ("{\"timestamp\":\"" + LocalDateTime.now()
                + "\",\"messages\":[\"Token revocado\"],\"status\":401}").getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
    }
}
