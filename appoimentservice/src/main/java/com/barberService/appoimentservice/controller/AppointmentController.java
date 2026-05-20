package com.barberService.appoimentservice.controller;

import com.barberService.appoimentservice.dto.AppointmentResponse;
import com.barberService.appoimentservice.dto.BookAppointmentRequest;
import com.barberService.appoimentservice.model.Appointment;
import com.barberService.appoimentservice.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public AppointmentResponse book(@RequestBody BookAppointmentRequest request) {
        Appointment appointment = appointmentService.bookAppointment(request);

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getStatus(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime()
        );
    }

    @PostMapping("/{id}/cancel")
    public void cancel(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
    }
}
