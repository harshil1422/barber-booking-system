package com.newlook.booking.mapper;

import com.newlook.booking.booking.domain.event.BookingCreated;
import com.newlook.booking.event.domain.BookingCreatedPayload;
import org.springframework.stereotype.Component;

@Component
public class BookingCreatedPayloadMapper {

    public BookingCreatedPayload map(
            BookingCreated event
    ) {

        return new BookingCreatedPayload(
                event.bookingId(),
                event.userId(),
                event.barberId(),
                event.shopId(),
                event.startTime(),
                event.endTime(),
                event.totalDurationMinutes(),
                event.totalAmount()
        );
    }
}