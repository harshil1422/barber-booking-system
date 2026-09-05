package com.newlook.booking.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateBookingRequest(

        @NotNull(message = "shopId is required")
        UUID shopId,

        @NotNull(message = "barberId is required")
        UUID barberId,

        @NotNull(message = "startTime is required")
        Instant startTime,

        @NotEmpty(message = "At least one service must be selected")
        List<
                        @NotNull(message = "serviceCatalogId cannot be null")
                                UUID
                        > serviceCatalogIds
) {
}
