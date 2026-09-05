package com.newlook.booking.booking.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BookingCreated(

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