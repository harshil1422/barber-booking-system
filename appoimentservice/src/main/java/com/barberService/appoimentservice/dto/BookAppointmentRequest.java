package com.barberService.appoimentservice.dto;


import java.time.*;


import jakarta.validation.constraints.*;
import java.time.*;

public record BookAppointmentRequest(

        @NotNull(message = "UserId is required")
        Long userId,

        @NotNull(message = "BarberShopId is required")
        Long barberShopId,

        @NotNull(message = "SlotId is required")
        Long slotId,

        @NotNull(message = "Appointment date is required")
        @FutureOrPresent(message = "Appointment date cannot be in the past")
        LocalDate appointmentDate,

        @NotNull(message = "Appointment time is required")
        LocalTime appointmentTime
) {}

