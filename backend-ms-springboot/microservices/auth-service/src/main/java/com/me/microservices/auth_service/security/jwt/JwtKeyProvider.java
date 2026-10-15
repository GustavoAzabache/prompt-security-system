package com.me.microservices.auth_service.security.jwt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtKeyProvider {

    private final JwtProperties jwtProperties;

    private PrivateKey privateKeyCache;
    private PublicKey publicKeyCache;

    public PrivateKey getPrivateKey() {
        if (privateKeyCache == null) {
            privateKeyCache = loadPrivateKey(jwtProperties.getPrivateKey());
        }
        return privateKeyCache;
    }

    public PublicKey getPublicKey() {
        if (publicKeyCache == null) {
            publicKeyCache = loadPublicKey(jwtProperties.getPublicKey());
        }
        return publicKeyCache;
    }

    private PrivateKey loadPrivateKey(Resource resource) {
        if (resource == null) {
            throw new IllegalStateException("jwt.private-key no está configurado (Resource es null). Revisa auth-service.yaml:21");
        }
        try {
            String pem = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String base64 = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(base64);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer jwt.private-key desde Resource: " + resource, e);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo parsear PRIVATE KEY (se esperaba PKCS#8). Verifica formato PEM", e);
        }
    }

    private PublicKey loadPublicKey(Resource resource) {
        if (resource == null) {
            throw new IllegalStateException("jwt.public-key no está configurado (Resource es null). Revisa auth-service.yaml:22");
        }
        try {
            String pem = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String base64 = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(base64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer jwt.public-key desde Resource: " + resource, e);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo parsear PUBLIC KEY (se esperaba X.509). Verifica formato PEM", e);
        }
    }
}
