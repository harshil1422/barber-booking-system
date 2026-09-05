package com.newlook.booking.controller;

import com.newlook.booking.booking.domain.Booking;
import com.newlook.booking.booking.service.BookingService;
import com.newlook.booking.dto.request.CreateBookingRequest;
import com.newlook.booking.dto.response.CreateBookingResponse;
import com.newlook.booking.mapper.CreateBookingResponseMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final CreateBookingResponseMapper responseMapper;

    public BookingController(
            BookingService bookingService,
            CreateBookingResponseMapper responseMapper
    ) {
        this.bookingService = bookingService;
        this.responseMapper = responseMapper;
    }

    @PostMapping
    public ResponseEntity<CreateBookingResponse> createBooking(

            @RequestHeader("X-User-Id")
            UUID userId,

            @RequestHeader(value = "X-Correlation-Id", required = false)
            String correlationId,

            @Valid @RequestBody
            CreateBookingRequest request
    ) {

        String finalCorrelationId =
                correlationId != null
                        ? correlationId
                        : UUID.randomUUID().toString();

        Booking booking = bookingService.createBooking(
                UUID.randomUUID(),
                userId,
                request.shopId(),
                request.barberId(),
                request.startTime(),
                request.serviceCatalogIds(),
                finalCorrelationId
        );

        CreateBookingResponse response =
                responseMapper.map(booking);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}