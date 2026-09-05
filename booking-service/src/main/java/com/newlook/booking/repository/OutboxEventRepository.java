package com.newlook.booking.repository;


import com.newlook.booking.event.outbox.OutboxEvent;
import com.newlook.booking.event.outbox.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, Long> {

    @Query(value = """
            SELECT *
            FROM outbox_events
            WHERE status = 'PENDING'
              AND (
                    next_attempt_at IS NULL
                    OR next_attempt_at <= :now
                  )
            ORDER BY created_at
            LIMIT :batchSize
            FOR UPDATE SKIP LOCKED
            """,
            nativeQuery = true)
    List<OutboxEvent> findEventsToProcess(
            Instant now,
            int batchSize
    );

    @Query("""
        SELECT e
        FROM OutboxEvent e
        WHERE e.status = com.newlook.booking.event.outbox.OutboxStatus.PROCESSING
          AND e.processingStartedAt < :threshold
        """)
    List<OutboxEvent> findStaleProcessingEvents(
            Instant threshold
    );

    @Modifying
    @Query("""
            UPDATE OutboxEvent e
            SET e.status = :publishedStatus,
                e.publishedAt = :publishedAt,
                e.processingStartedAt = null,
                e.lastError = null
            WHERE e.id = :id
              AND e.status = :processingStatus
            """)
    int markPublished(
            @Param("id") Long id,
            @Param("processingStatus")
            OutboxStatus processingStatus,
            @Param("publishedStatus")
            OutboxStatus publishedStatus,
            @Param("publishedAt")
            Instant publishedAt
    );

    @Modifying
    @Query("""
            UPDATE OutboxEvent e
            SET e.status = :pendingStatus,
                e.retryCount = e.retryCount + 1,
                e.nextAttemptAt = :nextAttemptAt,
                e.lastError = :error,
                e.processingStartedAt = null
            WHERE e.id = :id
              AND e.status = :processingStatus
            """)
    int markForRetry(
            @Param("id") Long id,
            @Param("processingStatus")
            OutboxStatus processingStatus,
            @Param("pendingStatus")
            OutboxStatus pendingStatus,
            @Param("nextAttemptAt")
            Instant nextAttemptAt,
            @Param("error")
            String error
    );
}
