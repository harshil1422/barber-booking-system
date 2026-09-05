package com.newlook.booking.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record AvailabilityRequest(
        @NotNull(message ="shopId is required")
        UUID shopId,

        @NotNull(message ="date is required")
        LocalDate date,

        UUID barberId,

        @NotEmpty(message = "At least one service must be selected")
        List<
                @NotNull(message = "serviceCatalogId cannot be null")
                UUID
                > serviceCatalogIds
) {

}
