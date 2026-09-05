package com.newlook.booking;


import com.newlook.booking.shared.properties.OutboxPublisherProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * New Look – Booking Service (:8084)
 *
 * Owns:
 *   - ChairSlot entity (not Chair service — slot lifecycle belongs here)
 *   - Booking entity with PENDING→CONFIRMED→IN_PROGRESS→COMPLETED→CANCELLED
 *   - WalkInQueue entity (physical walk-in customers)
 *   - IdempotencyKey table (prevents double-booking on network retry)
 *   - BookingOutbox table (Outbox pattern → Kafka via Debezium)
 */
@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
public class BookingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }
}