package com.barberService.auth_service.repository;

import com.barberService.auth_service.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog,Long> {
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
