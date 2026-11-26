package com.sentinelgate.service;

import com.sentinelgate.audit.AuditLogService;
import com.sentinelgate.dto.request.TelemetryCreateRequest;
import com.sentinelgate.dto.response.TelemetryResponse;
import com.sentinelgate.entity.Severity;
import com.sentinelgate.entity.TelemetryRecord;
import com.sentinelgate.exception.ResourceNotFoundException;
import com.sentinelgate.repository.TelemetryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;

/**
 * Kritik Altyapı Telemetri İş Mantığı Servisi.
 * Kriptografik SHA-256 veri bütünlüğü (Integrity Hash) doğrulaması ve otomatik Audit Loglama yapar.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryService {

    private final TelemetryRepository telemetryRepository;
    private final AuditLogService auditLogService;

    /**
     * Sensör telemetri verisini doğrular, SHA-256 bütünlük özeti hesaplar ve kaydeder.
     */
    @Transactional
    public TelemetryResponse recordTelemetry(TelemetryCreateRequest request, String submittedBy, String clientIp) {
        LocalDateTime now = LocalDateTime.now();
        String payloadHash = calculateSha256(request.getSensorId() + ":" +
                request.getFacilityZone() + ":" +
                request.getMetricType() + ":" +
                request.getMetricValue() + ":" +
                request.getUnit() + ":" +
                request.getSeverity().name() + ":" +
                now);

        TelemetryRecord record = TelemetryRecord.builder()
                .sensorId(request.getSensorId())
                .facilityZone(request.getFacilityZone())
                .metricType(request.getMetricType())
                .metricValue(request.getMetricValue())
                .unit(request.getUnit())
                .severity(request.getSeverity())
                .payloadHash(payloadHash)
                .recordedAt(now)
                .submittedBy(submittedBy)
                .build();

        TelemetryRecord saved = telemetryRepository.save(record);

        // Kritiklik seviyesine göre Audit Loglama
        if (request.getSeverity() == Severity.CRITICAL) {
            log.warn("KRİTİK TELEMETRİ ALARMI! Sensör: {}, Bölge: {}, Metrik: {} {}",
                    request.getSensorId(), request.getFacilityZone(), request.getMetricValue(), request.getUnit());

            auditLogService.log(
                    submittedBy,
                    "CRITICAL_TELEMETRY_ALERT",
                    "/api/v1/telemetry",
                    "POST",
                    201,
                    clientIp,
                    String.format("Sensör: %s, Bölge: %s, Metrik: %s, Değer: %s %s, Hash: %s",
                            request.getSensorId(), request.getFacilityZone(), request.getMetricType(),
                            request.getMetricValue(), request.getUnit(), payloadHash)
            );
        } else {
            auditLogService.log(
                    submittedBy,
                    "TELEMETRY_INGESTION",
                    "/api/v1/telemetry",
                    "POST",
                    201,
                    clientIp,
                    String.format("Sensör: %s, Seviye: %s", request.getSensorId(), request.getSeverity())
            );
        }

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<TelemetryResponse> getAllTelemetry(Pageable pageable) {
        return telemetryRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<TelemetryResponse> getCriticalTelemetry() {
        return telemetryRepository.findBySeverity(Severity.CRITICAL).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TelemetryResponse getTelemetryById(Long id) {
        TelemetryRecord record = telemetryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Telemetri kaydı bulunamadı. ID: " + id));
        return mapToResponse(record);
    }

    @Transactional(readOnly = true)
    public List<TelemetryResponse> getRecentTelemetry() {
        return telemetryRepository.findTop50ByOrderByRecordedAtDesc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TelemetryResponse mapToResponse(TelemetryRecord record) {
        return TelemetryResponse.builder()
                .id(record.getId())
                .sensorId(record.getSensorId())
                .facilityZone(record.getFacilityZone())
                .metricType(record.getMetricType())
                .metricValue(record.getMetricValue())
                .unit(record.getUnit())
                .severity(record.getSeverity())
                .payloadHash(record.getPayloadHash())
                .recordedAt(record.getRecordedAt())
                .submittedBy(record.getSubmittedBy())
                .build();
    }

    private String calculateSha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algoritması bulunamadı", e);
        }
    }
}
