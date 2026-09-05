package com.newlook.notification_service.consumer;


import com.newlook.notification_service.event.booking.BookingCreatedPayload;
import com.newlook.notification_service.event.model.EventEnvelope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class BookingEventConsumer {

    private final ObjectMapper objectMapper;
    private final BookingEventProcessor processor;

    public BookingEventConsumer(
            ObjectMapper objectMapper,
            BookingEventProcessor processor) {

        this.objectMapper = objectMapper;
        this.processor = processor;
    }

    @KafkaListener(
            topics = "booking.appointment.events",
            groupId = "notification-service"
    )
    public void consume(String message) throws Exception {

        EventEnvelope<BookingCreatedPayload> event =
                objectMapper.readValue(
                        message,
                        objectMapper.getTypeFactory()
                                .constructParametricType(
                                        EventEnvelope.class,
                                        BookingCreatedPayload.class
                                )
                );

        processor.process(event);
    }
}