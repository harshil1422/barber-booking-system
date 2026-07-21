package com.newlook.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * New Look – Notification Service (:8086)
 *
 * Architecture:
 *   Kafka topics  →  KafkaConsumer
 *                         │
 *                         ▼
 *                   NotificationService  ──→  PostgreSQL (persist)
 *                         │
 *                         ▼
 *                   RedisPublisher  ──→  Redis pub/sub channel "nl:notif:user:{userId}"
 *                                              │
 *                                              ▼
 *                                       SseEmitterRegistry
 *                                              │
 *                                              ▼
 *                                       Angular EventSource client
 *
 * Redis pub/sub bridges between Kafka consumer (any pod) and
 * SSE connections (tied to a specific pod), solving the multi-instance
 * SSE fan-out problem without sticky sessions.
 */
@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}