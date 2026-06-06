package com.barberService.auth_service.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        @NotBlank
        String secret,               // 256-bit base64 key — from env JWT_SECRET

        @Positive
        long accessTokenExpiry,      // seconds — 900 (15 min)

        @Positive
        long refreshTokenExpiry,     // seconds — 604800 (7 days)

        @NotBlank
        String issuer
) {
}
