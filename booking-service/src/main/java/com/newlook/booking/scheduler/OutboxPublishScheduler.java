package com.newlook.booking.scheduler;

import com.newlook.booking.event.outbox.OutboxEvent;
import com.newlook.booking.event.outbox.OutboxPublisher;
import com.newlook.booking.service.OutboxClaimService;
import com.newlook.booking.service.OutboxRecoveryService;
import com.newlook.booking.shared.properties.OutboxPublisherProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxPublishScheduler {

    private final OutboxClaimService claimService;
    private final OutboxRecoveryService  recoveryService;
    private final OutboxPublisher outboxPublisher;
    private final OutboxPublisherProperties publisherProperties;


    public OutboxPublishScheduler(OutboxClaimService claimService, OutboxRecoveryService recoveryService, OutboxPublisher outboxPublisher, OutboxPublisherProperties publisherProperties) {
        this.claimService = claimService;
        this.recoveryService = recoveryService;
        this.outboxPublisher = outboxPublisher;
        this.publisherProperties = publisherProperties;
    }

    @Scheduled( fixedDelayString = "${outbox.publisher.fixed-delay:1000}")
    public void publishOutboxEvents(){

        List<OutboxEvent> events = claimService.claimEvents(publisherProperties.getBatchSize());
        for(OutboxEvent event:events){
            outboxPublisher.publish(event);
        }

    }

    @Scheduled(
            fixedDelayString =
                    "${outbox.publisher.recovery-delay:60000}"
    )
    public void recover() {
        recoveryService.recoverStaleEvents();
    }
}
