package com.me.microservices.auth_service.common.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RenewalAccessTokenRequest(

    @NotBlank(message = "Refresh Token is required")
    String refreshToken

) {}
