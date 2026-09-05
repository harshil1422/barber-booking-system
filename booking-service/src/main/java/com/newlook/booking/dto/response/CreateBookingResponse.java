package com.newlook.booking.dto.response;

import com.newlook.booking.booking.domain.BookingStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateBookingResponse(

        UUID bookingId,

        UUID shopId,

        UUID barberId,

        Instant startTime,

        Instant endTime,

        int totalDurationMinutes,

        BigDecimal totalAmount,

        BookingStatus status

) {
}