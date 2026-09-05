package com.newlook.booking.service;

import com.newlook.booking.event.outbox.OutboxEvent;
import com.newlook.booking.event.outbox.OutboxStatus;
import com.newlook.booking.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class OutboxClaimService {

    private final OutboxEventRepository outboxEventRepository;

    public OutboxClaimService( OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    public List<OutboxEvent> claimEvents(int batchSize){
        Instant now = Instant.now();

        List<OutboxEvent> events =outboxEventRepository.findEventsToProcess(now,batchSize);

        Instant processingStartAt=Instant.now();
        for(OutboxEvent event:events){
            event.setStatus(OutboxStatus.PROCESSING);
            event.setProcessingStartedAt(processingStartAt);
        }
       return events;
    }


}
