package com.newlook.booking.booking.service;

import com.newlook.booking.booking.domain.Booking;
import com.newlook.booking.booking.domain.BookingItem;
import com.newlook.booking.booking.domain.event.BookingCreated;
import com.newlook.booking.catlog.model.ServiceCatalogDetails;
import com.newlook.booking.catlog.provider.ServiceCatalogProvider;
import com.newlook.booking.event.domain.BookingCreatedPayload;
import com.newlook.booking.event.model.EventEnvelope;
import com.newlook.booking.event.outbox.OutboxEvent;
import com.newlook.booking.event.outbox.OutboxEventMapper;
import com.newlook.booking.mapper.BookingCreatedPayloadMapper;
import com.newlook.booking.mapper.BookingItemMapper;
import com.newlook.booking.repository.BookingRepository;
import com.newlook.booking.repository.OutboxEventRepository;
import com.newlook.booking.shared.event.EventEnvelopeFactory;
import com.newlook.booking.shared.event.EventTypes;
import com.newlook.booking.shared.event.EventVersions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ServiceCatalogProvider serviceCatalogProvider;
    private final BookingItemMapper bookingItemMapper;
    private final BookingCreatedPayloadMapper payloadMapper;
    private final EventEnvelopeFactory envelopeFactory;
    private final OutboxEventMapper outboxEventMapper;

    public BookingService(
            BookingRepository bookingRepository,
            OutboxEventRepository outboxEventRepository,
            ServiceCatalogProvider serviceCatalogProvider,
            BookingItemMapper bookingItemMapper,
            BookingCreatedPayloadMapper payloadMapper,
            EventEnvelopeFactory envelopeFactory,
            OutboxEventMapper outboxEventMapper
    ) {
        this.bookingRepository = bookingRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.serviceCatalogProvider = serviceCatalogProvider;
        this.bookingItemMapper = bookingItemMapper;
        this.payloadMapper = payloadMapper;
        this.envelopeFactory = envelopeFactory;
        this.outboxEventMapper = outboxEventMapper;
    }

    @Transactional
    public Booking createBooking(
            UUID bookingId,
            UUID userId,
            UUID shopId,
            UUID barberId,
            Instant startTime,
            List<UUID> serviceCatalogIds,
            String correlationId
    ) {

        // 1. Get authoritative service details
        List<ServiceCatalogDetails> services =
                serviceCatalogProvider.getServices(
                        shopId,
                        serviceCatalogIds
                );

        // 2. Validate all requested services were found
        if (services.size() != serviceCatalogIds.size()) {
            throw new IllegalArgumentException(
                    "One or more requested services were not found"
            );
        }

        // 3. Convert service details to booking snapshots
        List<BookingItem> bookingItems =
                services.stream()
                        .map(bookingItemMapper::map)
                        .toList();

        // 4. Create aggregate
        Booking booking = Booking.create(
                bookingId,
                userId,
                barberId,
                shopId,
                startTime,
                bookingItems
        );

        // 5. Save aggregate
        bookingRepository.save(booking);

        // 6. Process domain events
        for (Object domainEvent : booking.getDomainEvents()) {

            if (domainEvent instanceof BookingCreated event) {

                BookingCreatedPayload payload =
                        payloadMapper.map(event);

                EventEnvelope<BookingCreatedPayload> envelope =
                        envelopeFactory.create(
                                EventTypes.BOOKING_CREATED,
                                EventVersions.V1,
                                "booking-service",
                                correlationId,
                                payload
                        );

                OutboxEvent outboxEvent =
                        outboxEventMapper.map(envelope);

                outboxEventRepository.save(outboxEvent);
            }
        }

        // 7. Clear in-memory domain events
        booking.clearDomainEvents();

        return booking;
    }
}