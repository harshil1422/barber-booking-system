package com.barberService.appoimentservice.dto;


import com.barberService.appoimentservice.model.AppointmentStatus;

import java.time.*;

public record AppointmentResponse(
        Long appointmentId,
        AppointmentStatus status,
        LocalDate appointmentDate,
        LocalTime appointmentTime
) {}
