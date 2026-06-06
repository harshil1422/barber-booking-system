package com.barberService.auth_service;


import com.barberService.auth_service.config.JwtProperties;
import com.barberService.auth_service.model.User;
import com.barberService.auth_service.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        // 32-byte (256-bit) key encoded in base64
        String secret = Base64.getEncoder().encodeToString(
                "test-secret-key-for-unit-testing!".getBytes());

        JwtProperties props = new JwtProperties(secret, 900L, 604800L, "test-app");
        jwtTokenProvider = new JwtTokenProvider(props);
    }

    @Test
    @DisplayName("generateAccessToken() produces a valid, parseable JWT")
    void generateAccessToken_producesValidJwt() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .password("hashed")
                .roles(Set.of())
                .build();

        String token = jwtTokenProvider.generateAccessToken(user, Map.of("userId", user.getId().toString()));

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);  // header.payload.signature
    }

    @Test
    @DisplayName("isTokenValid() returns true for a freshly generated token")
    void isTokenValid_freshToken_returnsTrue() {
        User user = User.builder()
                .id(UUID.randomUUID()).username("u").email("u@e.com")
                .password("p").roles(Set.of()).build();

        String token = jwtTokenProvider.generateAccessToken(user, Map.of());
        assertThat(jwtTokenProvider.isTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("isTokenValid() returns false for a tampered token")
    void isTokenValid_tamperedToken_returnsFalse() {
        assertThat(jwtTokenProvider.isTokenValid("invalid.token.value")).isFalse();
    }

    @Test
    @DisplayName("validateAndParseClaims() extracts subject correctly")
    void validateAndParseClaims_extractsSubject() {
        User user = User.builder()
                .id(UUID.randomUUID()).username("alice").email("alice@e.com")
                .password("p").roles(Set.of()).build();

        String token = jwtTokenProvider.generateAccessToken(user, Map.of());
        Claims claims = jwtTokenProvider.validateAndParseClaims(token);

        assertThat(claims.getSubject()).isEqualTo("alice");
        assertThat(claims.getIssuer()).isEqualTo("test-app");
    }
}
