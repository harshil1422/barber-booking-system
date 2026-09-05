package com.newlook.booking.service;

import com.newlook.booking.event.outbox.OutboxEvent;
import com.newlook.booking.repository.OutboxEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class OutboxRecoveryService {

    @Value("${outbox.publisher.processing-timeout}")
    private Duration processingTimeout;

    private final OutboxEventRepository eventRepository;

    public OutboxRecoveryService(OutboxEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void recoverStaleEvents(){
        Instant threshold = Instant.now().minus(processingTimeout);

        List<OutboxEvent> events =eventRepository.findStaleProcessingEvents(threshold);

        for (OutboxEvent event : events){
            event.markForRetry(Instant.now(),"Recovered stale processing event");
        }
    }
}
