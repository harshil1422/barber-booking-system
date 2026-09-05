package com.newlook.booking.booking.domain;

import com.newlook.booking.booking.domain.event.BookingCreated;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @Column(name = "booking_id", nullable = false, updatable = false)
    private UUID bookingId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "barber_id", nullable = false)
    private UUID barberId;

    @Column(name = "shop_id", nullable = false)
    private UUID shopId;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(name = "total_duration_minutes", nullable = false)
    private int totalDurationMinutes;

    @Column(
            name = "total_amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal totalAmount;



    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "booking_status")
    private BookingStatus status;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(
            name = "booking_id",
            nullable = false
    )
    private List<BookingItem> items = new ArrayList<>();

    @Transient
    private List<Object> domainEvents = new ArrayList<>();

    protected Booking() {
        // Required by JPA
    }

    private Booking(
            UUID bookingId,
            UUID userId,
            UUID barberId,
            UUID shopId,
            Instant startTime,
            List<BookingItem> items
    ) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.barberId = barberId;
        this.shopId = shopId;
        this.startTime = startTime;

        this.items.addAll(items);

        this.totalDurationMinutes = calculateTotalDuration(items);
        this.totalAmount = calculateTotalAmount(items);

        this.endTime = startTime.plusSeconds(
                totalDurationMinutes * 60L
        );

        this.status = BookingStatus.PENDING;
    }

    public static Booking create(
            UUID bookingId,
            UUID userId,
            UUID barberId,
            UUID shopId,
            Instant startTime,
            List<BookingItem> items
    ) {

        Booking booking = new Booking(
                bookingId,
                userId,
                barberId,
                shopId,
                startTime,
                items
        );

        booking.domainEvents.add(
                new BookingCreated(
                        booking.bookingId,
                        booking.userId,
                        booking.barberId,
                        booking.shopId,
                        booking.startTime,
                        booking.endTime,
                        booking.totalDurationMinutes,
                        booking.totalAmount
                )
        );

        return booking;
    }

    private int calculateTotalDuration(
            List<BookingItem> items
    ) {
        return items.stream()
                .mapToInt(BookingItem::getDurationMinutes)
                .sum();
    }

    private BigDecimal calculateTotalAmount(
            List<BookingItem> items
    ) {
        return items.stream()
                .map(BookingItem::getPrice)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getBarberId() {
        return barberId;
    }

    public UUID getShopId() {
        return shopId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public int getTotalDurationMinutes() {
        return totalDurationMinutes;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public List<BookingItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}