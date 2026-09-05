package com.newlook.booking.event.model;


import java.time.Instant;
import java.util.UUID;

public class EventEnvelope<T> {

    private final UUID eventId;
    private final String eventType;
    private final String eventVersion;
    private final Instant occurredAt;
    private final String producer;
    private final String correlationId;
    private final T payload;

    public EventEnvelope(
            UUID eventId,
            String eventType,
            String eventVersion,
            Instant occurredAt,
            String producer,
            String correlationId,
            T payload) {

        this.eventId = eventId;
        this.eventType = eventType;
        this.eventVersion = eventVersion;
        this.occurredAt = occurredAt;
        this.producer = producer;
        this.correlationId = correlationId;
        this.payload = payload;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventVersion() {
        return eventVersion;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getProducer() {
        return producer;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public T getPayload() {
        return payload;
    }
}
