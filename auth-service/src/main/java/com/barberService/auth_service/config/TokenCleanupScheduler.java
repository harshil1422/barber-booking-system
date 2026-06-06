package com.barberService.auth_service.config;


import com.barberService.auth_service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Runs daily at midnight.
     * Deletes expired or revoked refresh token rows to keep the table lean.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void purgeExpiredTokens() {
        int deleted = refreshTokenRepository
                .deleteByExpiresAtBeforeOrRevokedTrue(Instant.now());
        log.info("Token cleanup: removed {} expired/revoked refresh tokens", deleted);
    }
}