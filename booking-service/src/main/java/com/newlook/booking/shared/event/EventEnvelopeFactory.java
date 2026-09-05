package com.newlook.booking.shared.event;


import com.newlook.booking.event.model.EventEnvelope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class EventEnvelopeFactory {

    public <T> EventEnvelope<T> create(
            String eventType,
            String eventVersion,
            String producer,
            String correlationId,
            T payload) {

        return new EventEnvelope<>(
                UUID.randomUUID(),
                eventType,
                eventVersion,
                Instant.now(),
                producer,
                correlationId,
                payload
        );
    }
}
