package com.newlook.booking.mapper;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.newlook.booking.event.domain.BookingCreatedPayload;
import com.newlook.booking.event.model.EventEnvelope;
import com.newlook.booking.event.outbox.OutboxEvent;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventEnvelopeMapper {

    private final ObjectMapper objectMapper;

    public OutboxEventEnvelopeMapper(
            ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public EventEnvelope<BookingCreatedPayload> map(
            OutboxEvent outboxEvent) {

        BookingCreatedPayload payload =
                objectMapper.convertValue(
                        outboxEvent.getPayload(),
                        BookingCreatedPayload.class
                );

        return new EventEnvelope<>(
                outboxEvent.getEventId(),
                outboxEvent.getEventType(),
                outboxEvent.getEventVersion(),
                outboxEvent.getOccurredAt(),
                outboxEvent.getProducer(),
                outboxEvent.getCorrelationId(),
                payload
        );
    }
}
