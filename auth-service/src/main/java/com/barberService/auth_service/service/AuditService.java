package com.barberService.auth_service.service;


import com.barberService.auth_service.model.AuditLog;
import com.barberService.auth_service.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void log(UUID userId, String action, HttpServletRequest request) {
        log(userId, action, request, null);
    }

    @Async
    public void log(UUID userId, String action, HttpServletRequest request, Map<String, Object> meta) {
        try {
            AuditLog entry = AuditLog.builder()
                    .userId(userId)
                    .action(action)
                    .ipAddress(resolveClientIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .meta(meta)
                    .build();
            saveAuditLog(entry);
        } catch (Exception e) {
            // Audit failure must never break the main flow
            log.warn("Failed to write audit log for action={} userId={}: {}", action, userId, e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAuditLog(AuditLog entry){
        auditLogRepository.save(entry);
    }

    /** Resolve real client IP — handles reverse proxy X-Forwarded-For header. */
    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}