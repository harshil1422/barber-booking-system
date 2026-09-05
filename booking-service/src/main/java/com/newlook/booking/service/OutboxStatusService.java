package com.newlook.booking.service;

import com.newlook.booking.event.outbox.OutboxStatus;
import com.newlook.booking.repository.OutboxEventRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
@Service
public class OutboxStatusService {

    private final OutboxEventRepository eventRepository;

    public OutboxStatusService(OutboxEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public void  markPublished(Long outboxId){

        int updated =
                eventRepository.markPublished(
                        outboxId,
                        OutboxStatus.PENDING,
                        OutboxStatus.PUBLISHED,
                        Instant.now()
                );

        if (updated == 0) {
            // Event was no longer in PROCESSING state.
            // Another process may have recovered it.
        }
    }

    public void markRetry(long outboxId, String   errorMessage, long retryCount){
        Instant nextAttemptAt =
                calculateNextAttempt(retryCount);

        int updated =
                eventRepository.markForRetry(
                        outboxId,
                        OutboxStatus.PROCESSING,
                        OutboxStatus.PENDING,
                        nextAttemptAt,
                        errorMessage
                );

        if (updated == 0) {
            // Event was no longer in PROCESSING state.
        }
    }

    private Instant calculateNextAttempt(long retryCount){
        long delaySeconds =Math.min(300,(long)Math.pow(2,retryCount));
        return Instant.now().plusSeconds(delaySeconds);
    }
}
