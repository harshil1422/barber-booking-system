package com.newlook.booking.mapper;

import com.newlook.booking.booking.domain.Booking;
import com.newlook.booking.dto.response.CreateBookingResponse;
import org.springframework.stereotype.Component;

@Component
public class CreateBookingResponseMapper {

    public CreateBookingResponse map(Booking booking) {

        return new CreateBookingResponse(
                booking.getBookingId(),
                booking.getShopId(),
                booking.getBarberId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getTotalDurationMinutes(),
                booking.getTotalAmount(),
                booking.getStatus()
        );
    }
}