package com.newlook.notification_service.inbox.service;

import com.newlook.notification_service.inbox.domain.ProcessedEvent;
import com.newlook.notification_service.inbox.repository.ProcessedEventRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class EventIdempotencyService {

    private final ProcessedEventRepository processedEventRepository;

    public EventIdempotencyService(
            ProcessedEventRepository processedEventRepository) {

        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    public boolean isAlreadyProcessed(UUID eventId) {

        return processedEventRepository.existsByEventId(eventId);
    }

    @Transactional
    public void markProcessed(
            UUID eventId,
            String eventType) {

        ProcessedEvent processedEvent =
                ProcessedEvent.create(eventId, eventType);

        processedEventRepository.save(processedEvent);
    }
}
