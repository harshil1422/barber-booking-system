package com.newlook.notification_service.inbox.repository;

import com.newlook.notification_service.inbox.domain.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository
        extends JpaRepository<ProcessedEvent, UUID> {

    boolean existsByEventId(UUID eventId);
}