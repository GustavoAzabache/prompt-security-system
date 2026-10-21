package com.me.microservices.rag_service.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RevocationFilter extends OncePerRequestFilter {

    private static final String REVOKED_KEY_PREFIX = "revoked:";

    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            String jti = jwtAuthentication.getToken().getId();

            try {
                if (jti != null && Boolean.TRUE.equals(redisTemplate.hasKey(REVOKED_KEY_PREFIX + jti))) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(
                        "{\"timestamp\":\"" + LocalDateTime.now() + "\",\"messages\":[\"Token revocado\"],\"status\":401}"
                    );
                    return;
                }
            } catch (Exception exception) {
                log.warn("No se pudo verificar revocacion en Redis, se permite el request: {}", exception.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
