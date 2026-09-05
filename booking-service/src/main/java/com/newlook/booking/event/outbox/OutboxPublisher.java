package com.newlook.booking.event.outbox;

import com.newlook.booking.event.domain.BookingCreatedPayload;
import com.newlook.booking.event.model.EventEnvelope;
import com.newlook.booking.mapper.OutboxEventEnvelopeMapper;
import com.newlook.booking.service.OutboxStatusService;
import com.newlook.booking.shared.event.TopicType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OutboxPublisher {


    private final KafkaTemplate<String ,Object>kafkaTemplate;
    private final OutboxEventEnvelopeMapper envelopeMapper;
    private final OutboxStatusService statusService;

    public OutboxPublisher(KafkaTemplate<String, Object> kafkaTemplate, OutboxEventEnvelopeMapper envelopeMapper, OutboxStatusService statusService) {
        this.kafkaTemplate = kafkaTemplate;
        this.envelopeMapper = envelopeMapper;
        this.statusService = statusService;
    }

    public void publish(OutboxEvent outboxEvent){
        long outboxId=outboxEvent.getId();
        long retryCount=outboxEvent.getRetryCount();
        EventEnvelope<BookingCreatedPayload> envelope = envelopeMapper.map(outboxEvent);
        String key=envelope.getPayload().bookingId().toString();

       kafkaTemplate.send(TopicType.APPOINTMENT_EVENTS_TOPIC, key, envelope)
               .whenComplete((result,exception) ->{
                               if(exception == null) {
                                   statusService.markPublished(outboxId);
                               }else{
                                  statusService.markRetry(outboxId,exception.getMessage(),retryCount);
                               }
       });


    }
}
