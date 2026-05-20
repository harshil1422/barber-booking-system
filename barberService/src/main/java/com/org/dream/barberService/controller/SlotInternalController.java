package com.org.dream.barberService.controller;

import com.org.dream.barberService.services.SlotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/slots")
public class SlotInternalController {

    private final SlotService slotService;

    public SlotInternalController(SlotService slotService) {
        this.slotService = slotService;
    }

    @PostMapping("/{slotId}/book")
    public ResponseEntity<?> book(@PathVariable Long slotId) {
        slotService.bookSlot(slotId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/start")
    public void start(){
        System.out.println("start");
    }

    
}

