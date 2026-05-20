package com.org.dream.barberService.repo;

import com.org.dream.barberService.model.SlotStatus;
import com.org.dream.barberService.model.TimeSlot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot ,Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from TimeSlot s where s.id = :id")
    Optional<TimeSlot> lockById(@Param("id") Long id);

    List<TimeSlot> findByShopIdAndSlotDateAndStatus(
            Long shopId, LocalDate date, SlotStatus status);
}
