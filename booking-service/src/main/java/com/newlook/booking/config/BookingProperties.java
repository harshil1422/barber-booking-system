package com.newlook.booking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Type-safe binding for newlook.booking.* in application.yml.
 */
@ConfigurationProperties(prefix = "newlook.booking")
public record BookingProperties(
        int    idempotencyTtlHours,
        int    defaultCancelWindowMinutes,
        int    walkinQueueMax,
        int    slotGenerationDaysAhead,
        String chairServiceUrl,
        String shopServiceUrl
) {} 