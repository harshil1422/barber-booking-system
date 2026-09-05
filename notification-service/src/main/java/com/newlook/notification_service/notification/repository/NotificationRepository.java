package com.newlook.notification_service.notification.repository;

import com.newlook.notification_service.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {
}
