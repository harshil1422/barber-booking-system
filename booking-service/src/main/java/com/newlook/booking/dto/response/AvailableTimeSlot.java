package com.newlook.booking.dto.response;

import java.time.Instant;
import java.util.UUID;

public record AvailableTimeSlot(
        UUID barberId,
        Instant startTime,
        Instant endTime
) {
}
