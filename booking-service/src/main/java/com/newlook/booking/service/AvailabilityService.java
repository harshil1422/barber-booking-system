package com.newlook.booking.service;

import com.newlook.booking.dto.request.AvailabilityRequest;
import com.newlook.booking.dto.response.AvailabilityResponse;
import org.springframework.stereotype.Service;

@Service
public interface AvailabilityService {
    AvailabilityResponse searchAvailability(
            AvailabilityRequest request
    );
}
