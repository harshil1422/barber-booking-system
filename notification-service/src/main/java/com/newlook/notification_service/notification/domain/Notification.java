package com.newlook.notification_service.notification.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "notifications",
        schema = "notification_db",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_notification_event_type",
                        columnNames = {"event_id", "notification_type"}
                )
        }
)
public class Notification {

    @Id
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "notification_type", nullable = false, length = 50)
    private String notificationType;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "notification_db.notification_status"
    )
    private NotificationStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    protected Notification() {
    }

    private Notification(
            UUID id,
            UUID eventId,
            UUID userId,
            String notificationType,
            String message,
            NotificationStatus status,
            Instant createdAt) {

        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.notificationType = notificationType;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Notification create(
            UUID eventId,
            UUID userId,
            String notificationType,
            String message) {

        return new Notification(
                UUID.randomUUID(),
                eventId,
                userId,
                notificationType,
                message,
                NotificationStatus.PENDING,
                Instant.now()
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public String getMessage() {
        return message;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getSentAt() {
        return sentAt;
    }
}