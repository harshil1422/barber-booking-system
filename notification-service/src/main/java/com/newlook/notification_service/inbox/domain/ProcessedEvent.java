package com.newlook.notification_service.inbox.domain;


import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "processed_events",
        schema = "notification_db",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_processed_event_id",
                        columnNames = "event_id"
                )
        }
)
public class ProcessedEvent {

    @Id
    private UUID id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    protected ProcessedEvent() {
    }

    private ProcessedEvent(
            UUID id,
            UUID eventId,
            String eventType,
            Instant processedAt) {

        this.id = id;
        this.eventId = eventId;
        this.eventType = eventType;
        this.processedAt = processedAt;
    }

    public static ProcessedEvent create(
            UUID eventId,
            String eventType) {

        return new ProcessedEvent(
                UUID.randomUUID(),
                eventId,
                eventType,
                Instant.now()
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }
}