package com.barberService.appoimentservice.client;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "slot-service", url = "${slot.service.url}")
public interface SlotServiceClient {

    @PostMapping("/internal/slots/{slotId}/book")
    void bookSlot(@PathVariable Long slotId);

    @PostMapping("/internal/slots/{slotId}/release")
    void releaseSlot(@PathVariable Long slotId);
}
