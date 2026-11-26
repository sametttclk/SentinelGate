package com.sentinelgate.service;

import com.sentinelgate.audit.AuditLogService;
import com.sentinelgate.dto.request.TelemetryCreateRequest;
import com.sentinelgate.dto.response.TelemetryResponse;
import com.sentinelgate.entity.Severity;
import com.sentinelgate.entity.TelemetryRecord;
import com.sentinelgate.repository.TelemetryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Birim Testi: TelemetryService")
class TelemetryServiceTest {

    @Mock
    private TelemetryRepository telemetryRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private TelemetryService telemetryService;

    @Test
    @DisplayName("Telemetri verisi kaydedildiğinde SHA-256 mühür hash'i üretilmeli ve AuditLog tetiklenmelidir")
    void shouldRecordTelemetryAndGenerateHash() {
        TelemetryCreateRequest request = TelemetryCreateRequest.builder()
                .sensorId("RADAR-ALPHA")
                .facilityZone("ZONE-NORTH")
                .metricType("ROTATION_SPEED")
                .metricValue(65.0)
                .unit("RPM")
                .severity(Severity.INFO)
                .build();

        TelemetryRecord saved = TelemetryRecord.builder()
                .id(1L)
                .sensorId(request.getSensorId())
                .facilityZone(request.getFacilityZone())
                .metricType(request.getMetricType())
                .metricValue(request.getMetricValue())
                .unit(request.getUnit())
                .severity(request.getSeverity())
                .payloadHash("dummySha256HashValue1234567890abcdef")
                .recordedAt(LocalDateTime.now())
                .submittedBy("sensor-01")
                .build();

        when(telemetryRepository.save(any(TelemetryRecord.class))).thenReturn(saved);

        TelemetryResponse response = telemetryService.recordTelemetry(request, "sensor-01", "127.0.0.1");

        assertNotNull(response);
        assertEquals("RADAR-ALPHA", response.getSensorId());
        assertNotNull(response.getPayloadHash());
        verify(telemetryRepository, times(1)).save(any(TelemetryRecord.class));
        verify(auditLogService, times(1)).log(
                eq("sensor-01"), eq("TELEMETRY_INGESTION"), anyString(), eq("POST"), eq(201), eq("127.0.0.1"), anyString()
        );
    }

    @Test
    @DisplayName("CRITICAL telemetri verisi geldiğinde CRITICAL_TELEMETRY_ALERT loglanmalıdır")
    void shouldLogCriticalAlertWhenSeverityIsCritical() {
        TelemetryCreateRequest request = TelemetryCreateRequest.builder()
                .sensorId("PERIMETER-04")
                .facilityZone("BORDER-EAST")
                .metricType("SEISMIC_VIBRATION")
                .metricValue(9.4)
                .unit("MM_S")
                .severity(Severity.CRITICAL)
                .build();

        TelemetryRecord saved = TelemetryRecord.builder()
                .id(2L)
                .sensorId(request.getSensorId())
                .facilityZone(request.getFacilityZone())
                .metricType(request.getMetricType())
                .metricValue(request.getMetricValue())
                .unit(request.getUnit())
                .severity(Severity.CRITICAL)
                .payloadHash("criticalSha256Hash123")
                .recordedAt(LocalDateTime.now())
                .submittedBy("sensor-01")
                .build();

        when(telemetryRepository.save(any(TelemetryRecord.class))).thenReturn(saved);

        telemetryService.recordTelemetry(request, "sensor-01", "192.168.1.50");

        verify(auditLogService, times(1)).log(
                eq("sensor-01"), eq("CRITICAL_TELEMETRY_ALERT"), anyString(), eq("POST"), eq(201), eq("192.168.1.50"), anyString()
        );
    }
}
