package com.newlook.booking.event.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BookingCreatedPayload(

        UUID bookingId,

        UUID userId,

        UUID barberId,

        UUID shopId,

        Instant startTime,

        Instant endTime,

        int totalDurationMinutes,

        BigDecimal totalAmount

) {
}