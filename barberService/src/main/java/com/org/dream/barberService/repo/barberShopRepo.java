package com.org.dream.barberService.repo;

import com.org.dream.barberService.model.BarberShop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface barberShopRepo extends JpaRepository<BarberShop,Long> {
}
