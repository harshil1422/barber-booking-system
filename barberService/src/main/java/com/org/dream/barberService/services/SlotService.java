package com.org.dream.barberService.services;

import com.org.dream.barberService.model.SlotStatus;
import com.org.dream.barberService.model.TimeSlot;
import com.org.dream.barberService.repo.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SlotService {

    private final TimeSlotRepository repository;

    public SlotService(TimeSlotRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void bookSlot(Long slotId) {

        TimeSlot slot = repository.lockById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new RuntimeException("Slot already booked");
        }

        slot.setStatus(SlotStatus.BOOKED);
        repository.save(slot);
    }
}

