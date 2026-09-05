package com.newlook.booking.dto.response;

import java.time.LocalDate;
import java.util.List;

public record AvailabilityResponse(
        LocalDate date,
        int requiredDurationMinutes,
        List<AvailabilityResponse> availableSlots
) {

}
