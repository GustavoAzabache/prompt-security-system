package com.me.microservices.auth_service.security.jwt;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.me.microservices.auth_service.security.exception.InvalidTokenTypeException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final JwtKeyProvider jwtKeyProvider;

    public String generateExternalAccessToken(String userId, String jti) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpirationAccess());

        return Jwts.builder()
                .subject(userId)
                .id(jti)
                .claim("token_type", "access")
                .issuedAt(now)
                .expiration(expiryDate)
                .issuer(jwtProperties.getIssuer())
                .signWith(jwtKeyProvider.getPrivateKey())
                .compact();
    }

    public String generateEmployeeAccessToken(String userId, String area, String hierarchy, String jti) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpirationAccess());

        return Jwts.builder()
                .subject(userId)
                .id(jti)
                .claim("token_type", "access")
                .claim("area", area)
                .claim("hierarchy", hierarchy)
                .issuedAt(now)
                .expiration(expiryDate)
                .issuer(jwtProperties.getIssuer())
                .signWith(jwtKeyProvider.getPrivateKey())
                .compact();
    }

    public String generateRefreshToken(String userId, String jti) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpirationRefresh());

        return Jwts.builder()
                .subject(userId)
                .id(jti)
                .claim("token_type", "refresh")
                .issuedAt(now)
                .expiration(expiryDate)
                .issuer(jwtProperties.getIssuer())
                .signWith(jwtKeyProvider.getPrivateKey())
                .compact();
    }

    public Claims validateAccessToken(String token) {
        Claims claims = parseToken(token);

        validateTokenType(claims, "access");

        return claims;
    }

    public Claims validateRefreshToken(String token) {
        Claims claims = parseToken(token);

        validateTokenType(claims, "refresh");

        return claims;
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(jwtKeyProvider.getPublicKey())
                .requireIssuer(jwtProperties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public void validateTokenType(Claims claims, String expectedType) {
        String type = claims.get("token_type", String.class);

        if (!expectedType.equals(type)) {
            throw new InvalidTokenTypeException("The token type is invalid");
        }
    }

}
