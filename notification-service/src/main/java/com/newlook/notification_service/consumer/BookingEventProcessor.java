package com.newlook.notification_service.consumer;


import com.newlook.notification_service.event.booking.BookingCreatedPayload;
import com.newlook.notification_service.event.model.EventEnvelope;
import com.newlook.notification_service.inbox.domain.ProcessedEvent;
import com.newlook.notification_service.inbox.repository.ProcessedEventRepository;
import com.newlook.notification_service.notification.domain.Notification;
import com.newlook.notification_service.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingEventProcessor {

    private static final String BOOKING_CREATED =
            "booking.created";

    private static final String NOTIFICATION_TYPE =
            "BOOKING_CREATED";

    private final ProcessedEventRepository processedEventRepository;
    private final NotificationRepository notificationRepository;

    public BookingEventProcessor(
            ProcessedEventRepository processedEventRepository,
            NotificationRepository notificationRepository) {

        this.processedEventRepository = processedEventRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void process(
            EventEnvelope<BookingCreatedPayload> event) {

        /*
         * 1. Check whether this Kafka event
         *    has already been processed.
         */
        if (processedEventRepository
                .existsByEventId(event.getEventId())) {

            return;
        }

        /*
         * 2. Validate event type.
         */
        if (!BOOKING_CREATED.equals(event.getEventType())) {
            return;
        }

        BookingCreatedPayload payload =
                event.getPayload();

        /*
         * 3. Create notification.
         */
        Notification notification =
                Notification.create(
                        event.getEventId(),
                        payload.getUserId(),
                        NOTIFICATION_TYPE,
                        buildMessage(payload)
                );

        notificationRepository.save(notification);

        /*
         * 4. Mark Kafka event as processed.
         */
        ProcessedEvent processedEvent =
                ProcessedEvent.create(
                        event.getEventId(),
                        event.getEventType()
                );

        processedEventRepository.save(processedEvent);
    }

    private String buildMessage(
            BookingCreatedPayload payload) {

        return "Your booking has been created successfully. "
                + "Booking ID: "
                + payload.getBookingId();
    }
}
