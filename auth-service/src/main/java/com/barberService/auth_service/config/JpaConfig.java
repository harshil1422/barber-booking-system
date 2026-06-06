package com.barberService.auth_service.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Instant;
import java.util.Optional;

    @Configuration
    @EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
    public class JpaConfig {

        // Use Instant for all @CreatedDate / @LastModifiedDate fields
        @Bean
        public DateTimeProvider auditingDateTimeProvider() {
            return () -> Optional.of(Instant.now());
        }
    }


