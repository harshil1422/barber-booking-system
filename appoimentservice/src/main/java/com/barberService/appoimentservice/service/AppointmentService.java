package com.barberService.appoimentservice.service;

import com.barberService.appoimentservice.client.SlotServiceClient;
import com.barberService.appoimentservice.dto.BookAppointmentRequest;
import com.barberService.appoimentservice.exception.AppointmentNotFoundException;
import com.barberService.appoimentservice.exception.InvalidAppointmentStateException;
import com.barberService.appoimentservice.model.Appointment;
import com.barberService.appoimentservice.model.AppointmentStatus;
import com.barberService.appoimentservice.repo.AppointmentRepository;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final SlotServiceClient slotServiceClient;

    @Transactional
    public Appointment bookAppointment(BookAppointmentRequest request) {

        log.info(
                "Booking attempt started | userId={} | barberShopId={} | slotId={}",
                request.userId(), request.barberShopId(), request.slotId()
        );

        // 1. Book slot
        slotServiceClient.bookSlot(request.slotId());
        log.info("Slot successfully booked | slotId={}", request.slotId());

        try {
            Appointment appointment = Appointment.builder()
                    .userId(request.userId())
                    .barberShopId(request.barberShopId())
                    .slotId(request.slotId())
                    .appointmentDate(request.appointmentDate())
                    .appointmentTime(request.appointmentTime())
                    .status(AppointmentStatus.BOOKED)
                    .build();

            Appointment saved = repository.save(appointment);

            log.info(
                    "Appointment booked successfully | appointmentId={} | slotId={}",
                    saved.getId(), saved.getSlotId()
            );

            return saved;

        } catch (Exception ex) {

            log.error(
                    "Appointment save failed | rolling back slot | slotId={}",
                    request.slotId(),
                    ex
            );

            slotServiceClient.releaseSlot(request.slotId());
            log.warn("Slot released after failure | slotId={}", request.slotId());

            throw ex;
        }
    }

    @Transactional
    public void cancelAppointment(Long appointmentId) {

        log.info("Cancellation request received | appointmentId={}", appointmentId);

        Appointment appointment = repository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            log.warn(
                    "Invalid cancel attempt | appointmentId={} | status={}",
                    appointmentId, appointment.getStatus()
            );
            throw new InvalidAppointmentStateException(
                    "Only BOOKED appointments can be cancelled"
            );
        }

        slotServiceClient.releaseSlot(appointment.getSlotId());
        log.info(
                "Slot released due to cancellation | slotId={}",
                appointment.getSlotId()
        );

        appointment.setStatus(AppointmentStatus.CANCELLED);
        log.info(
                "Appointment cancelled successfully | appointmentId={}",
                appointmentId
        );
    }
    @Retry(name = "slotService", fallbackMethod = "bookSlotFallback")
    @TimeLimiter(name = "slotService")
    public void bookSlotWithRetry(Long slotId) {
        slotServiceClient.bookSlot(slotId);
    }

    private void bookSlotFallback(Long slotId, Throwable t) {
        log.error("Slot Service unavailable | slotId={} | reason={}", slotId, t.getMessage());
        throw new RuntimeException("Slot Service unavailable, try again later");
    }

    @Retry(name = "slotService", fallbackMethod = "releaseSlotFallback")
    @TimeLimiter(name = "slotService")
    public void releaseSlotWithRetry(Long slotId) {
        slotServiceClient.releaseSlot(slotId);
    }

    private void releaseSlotFallback(Long slotId, Throwable t) {
        log.error("Failed to release slot | slotId={} | reason={}", slotId, t.getMessage());
    }
}

