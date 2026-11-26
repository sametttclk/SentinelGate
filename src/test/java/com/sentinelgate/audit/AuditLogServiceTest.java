package com.sentinelgate.audit;

import com.sentinelgate.dto.response.AuditLogResponse;
import com.sentinelgate.entity.AuditLog;
import com.sentinelgate.repository.AuditLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Birim Testi: AuditLogService")
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    @Test
    @DisplayName("AuditLog başarıyla oluşturulmalı ve veritabanına kaydedilmelidir")
    void shouldLogAuditRecordSuccessfully() {
        auditLogService.log(
                "operator",
                "SECURITY_ACCESS_DENIED",
                "/api/v1/audit-logs",
                "GET",
                403,
                "192.168.1.100",
                "Yetkisiz rol ile erişim denemesi"
        );

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository, times(1)).save(captor.capture());

        AuditLog savedLog = captor.getValue();
        assertEquals("operator", savedLog.getActorUsername());
        assertEquals("SECURITY_ACCESS_DENIED", savedLog.getAction());
        assertEquals("/api/v1/audit-logs", savedLog.getEndpoint());
        assertEquals("GET", savedLog.getHttpMethod());
        assertEquals(403, savedLog.getStatusCode());
        assertEquals("192.168.1.100", savedLog.getIpAddress());
        assertEquals("Yetkisiz rol ile erişim denemesi", savedLog.getDetails());
        assertNotNull(savedLog.getTimestamp());
    }

    @Test
    @DisplayName("Kullanıcı adı null geldiğinde ANONYMOUS olarak kaydedilmelidir")
    void shouldLogAsAnonymousWhenActorIsNull() {
        auditLogService.log(
                null,
                "SECURITY_UNAUTHORIZED_ACCESS",
                "/api/v1/telemetry",
                "POST",
                401,
                "10.0.0.1",
                "Token olmadan erişim denendi"
        );

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository, times(1)).save(captor.capture());

        AuditLog savedLog = captor.getValue();
        assertEquals("ANONYMOUS", savedLog.getActorUsername());
    }

    @Test
    @DisplayName("Veritabanı hatasında exception fırlatılmamalı, loglama sessizce hata kaydetmelidir")
    void shouldHandleExceptionGracefullyWithoutThrowing() {
        when(auditLogRepository.save(any(AuditLog.class))).thenThrow(new RuntimeException("DB Bağlantı koptu"));

        assertDoesNotThrow(() -> auditLogService.log(
                "admin", "SYS_EVENT", "/api/v1/test", "GET", 200, "127.0.0.1", "Detay"
        ));
    }

    @Test
    @DisplayName("Filtrelenmiş AuditLog kayıtları sayfalı olarak dönmelidir")
    void shouldReturnFilteredAuditLogs() {
        AuditLog sample = AuditLog.builder()
                .id(1L)
                .timestamp(LocalDateTime.now())
                .actorUsername("admin")
                .action("SECURITY_ACCESS_DENIED")
                .endpoint("/api/v1/telemetry")
                .httpMethod("POST")
                .statusCode(403)
                .ipAddress("127.0.0.1")
                .details("Erişim reddedildi")
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<AuditLog> page = new PageImpl<>(Collections.singletonList(sample));

        when(auditLogRepository.findFiltered("admin", 403, "SECURITY_ACCESS_DENIED", pageable))
                .thenReturn(page);

        Page<AuditLogResponse> result = auditLogService.getAuditLogs("admin", 403, "SECURITY_ACCESS_DENIED", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        AuditLogResponse res = result.getContent().get(0);
        assertEquals("admin", res.getActorUsername());
        assertEquals(403, res.getStatusCode());
        assertEquals("SECURITY_ACCESS_DENIED", res.getAction());
    }
}
