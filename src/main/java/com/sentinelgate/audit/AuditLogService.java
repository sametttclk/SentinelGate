package com.sentinelgate.audit;

import com.sentinelgate.dto.response.AuditLogResponse;
import com.sentinelgate.entity.AuditLog;
import com.sentinelgate.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Güvenlik ve denetim kayıt servisi.
 * Ayrı bir transaction (REQUIRES_NEW) ile çalışarak ana işlem rollback olsa bile kaydı garanti eder.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String actorUsername, String action, String endpoint, 
                    String httpMethod, int statusCode, String ipAddress, String details) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .timestamp(LocalDateTime.now())
                    .actorUsername(actorUsername != null ? actorUsername : "ANONYMOUS")
                    .action(action)
                    .endpoint(endpoint)
                    .httpMethod(httpMethod)
                    .statusCode(statusCode)
                    .ipAddress(ipAddress)
                    .details(details)
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("AuditLog kaydedildi: [{} - {}] {} -> {}", action, statusCode, actorUsername, endpoint);
        } catch (Exception ex) {
            log.error("AuditLog kaydedilirken kritik hata oluştu: {}", ex.getMessage(), ex);
        }
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogs(String actorUsername, Integer statusCode, String action, Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findFiltered(actorUsername, statusCode, action, pageable);
        return page.map(this::mapToResponse);
    }

    private AuditLogResponse mapToResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .timestamp(log.getTimestamp())
                .actorUsername(log.getActorUsername())
                .action(log.getAction())
                .endpoint(log.getEndpoint())
                .httpMethod(log.getHttpMethod())
                .statusCode(log.getStatusCode())
                .ipAddress(log.getIpAddress())
                .details(log.getDetails())
                .build();
    }
}
