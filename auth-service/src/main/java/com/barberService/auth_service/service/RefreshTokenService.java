package com.barberService.auth_service.service;


import com.barberService.auth_service.config.JwtProperties;
import com.barberService.auth_service.exception.InvalidTokenException;
import com.barberService.auth_service.model.RefreshToken;
import com.barberService.auth_service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties props;

    /**
     * Creates a new refresh token for the given user.
     * Only the SHA-256 hash is stored — raw token returned to caller once.
     */
    public String createRefreshToken(UUID userId, String userAgent, String ipAddress) {
        // Two UUIDs concatenated = 73 chars of entropy
        String rawToken = UUID.randomUUID() + "-" + UUID.randomUUID();
        String hash = sha256Hex(rawToken);

        RefreshToken token = RefreshToken.builder()
                .tokenHash(hash)
                .userId(userId)
                .expiresAt(Instant.now().plusSeconds(props.refreshTokenExpiry()))
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .build();

        refreshTokenRepository.save(token);
        return rawToken;
    }

    /**
     * Rotates a refresh token:
     * 1. Finds the stored token by hash
     * 2. Detects reuse of already-revoked tokens (theft signal) → revokes all sessions
     * 3. Marks current token revoked
     * 4. Issues and returns a new raw token
     */
    public RotationResult rotate(String rawToken, String userAgent, String ipAddress) {
        String hash = sha256Hex(rawToken);

        RefreshToken existing = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        // Reuse of revoked token = likely theft — revoke all user sessions immediately
        if (existing.isRevoked()) {
            refreshTokenRepository.revokeAllByUserId(existing.getUserId());
            throw new InvalidTokenException("Token reuse detected — all sessions have been revoked");
        }

        if (existing.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidTokenException("Refresh token has expired");
        }

        // Revoke old token
        existing.setRevoked(true);
        refreshTokenRepository.save(existing);

        // Issue new token
        String newRawToken = createRefreshToken(existing.getUserId(), userAgent, ipAddress);
        return new RotationResult(existing.getUserId(), newRawToken);
    }

    public void revokeAll(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    public String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public record RotationResult(UUID userId, String newRawToken) {}
}