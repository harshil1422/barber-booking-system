package com.newlook.booking.mapper;

import com.newlook.booking.booking.domain.BookingItem;

import com.newlook.booking.catlog.model.ServiceCatalogDetails;
import org.springframework.stereotype.Component;

@Component
public class BookingItemMapper {

    public BookingItem map(ServiceCatalogDetails service) {

        return BookingItem.create(
                service.serviceCatalogId(),
                service.serviceName(),
                service.durationMinutes(),
                service.price()
        );
    }
}