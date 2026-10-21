package com.me.microservices.auth_service.security.jwt;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String REVOKED_KEY_PREFIX = "revoked:";

    private final JwtService jwtService;
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    )   throws ServletException, IOException {
        
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        try {
            Claims claims = jwtService.validateAccessToken(token);

            if (isRevoked(claims.getId())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(
                    "{\"timestamp\":\"" + LocalDateTime.now() + "\",\"messages\":[\"Token revocado\"],\"status\":401}"
                );
                return;
            }

            UUID accountId = UUID.fromString(claims.getSubject());
            String hierarchy = claims.get("hierarchy", String.class);
            List<GrantedAuthority> authorities;

            if (hierarchy == null) {
                authorities = Collections.emptyList();
            } else {
                authorities = List.of(new SimpleGrantedAuthority("ROLE_" + hierarchy));
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                                                accountId,
                                                null,
                                                authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRevoked(String jti) {
        if (jti == null) {
            return false;
        }

        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(REVOKED_KEY_PREFIX + jti));
        } catch (Exception exception) {
            log.warn("No se pudo verificar revocacion en Redis, se permite el request: {}", exception.getMessage());
            return false;
        }
    }
}
