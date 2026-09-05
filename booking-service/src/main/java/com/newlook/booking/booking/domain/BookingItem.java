package com.newlook.booking.booking.domain;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "booking_items")
public class BookingItem {

    @Id
    @Column(name = "booking_item_id", nullable = false, updatable = false)
    private UUID bookingItemId;

    @Column(name = "service_catalog_id", nullable = false)
    private UUID serviceCatalogId;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    protected BookingItem() {
        // Required by JPA
    }

    private BookingItem(
            UUID bookingItemId,
            UUID serviceCatalogId,
            String serviceName,
            int durationMinutes,
            BigDecimal price
    ) {
        this.bookingItemId = bookingItemId;
        this.serviceCatalogId = serviceCatalogId;
        this.serviceName = serviceName;
        this.durationMinutes = durationMinutes;
        this.price = price;
    }

    public static BookingItem create(
            UUID serviceCatalogId,
            String serviceName,
            int durationMinutes,
            BigDecimal price
    ) {

        return new BookingItem(
                UUID.randomUUID(),
                serviceCatalogId,
                serviceName,
                durationMinutes,
                price
        );
    }

    public UUID getBookingItemId() {
        return bookingItemId;
    }

    public UUID getServiceCatalogId() {
        return serviceCatalogId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public BigDecimal getPrice() {
        return price;
    }
}