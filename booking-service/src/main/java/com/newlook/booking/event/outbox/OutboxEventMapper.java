package com.newlook.booking.event.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newlook.booking.event.model.EventEnvelope;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OutboxEventMapper {

    private final ObjectMapper objectMapper;

    public OutboxEventMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> OutboxEvent map(EventEnvelope<T> envelope) {

        JsonNode payload =
                objectMapper.valueToTree(envelope.getPayload());

        OutboxEvent outboxEvent = new OutboxEvent();

        outboxEvent.setEventId(envelope.getEventId());
        outboxEvent.setEventType(envelope.getEventType());
        outboxEvent.setEventVersion(envelope.getEventVersion());
        outboxEvent.setOccurredAt(envelope.getOccurredAt());
        outboxEvent.setProducer(envelope.getProducer());
        outboxEvent.setCorrelationId(envelope.getCorrelationId());

        outboxEvent.setPayload(payload);

        outboxEvent.setStatus(OutboxStatus.PENDING);
        outboxEvent.setCreatedAt(Instant.now());
        outboxEvent.setRetryCount(0);

        return outboxEvent;
    }
}
