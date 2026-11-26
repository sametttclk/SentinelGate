package com.sentinelgate.controller;

import com.sentinelgate.dto.request.TelemetryCreateRequest;
import com.sentinelgate.dto.response.TelemetryResponse;
import com.sentinelgate.service.TelemetryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/telemetry")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Kritik Altyapı Telemetri", description = "Sensör telemetri veri alımı ve komuta kontrol merkezi sorgulama uçları")
public class TelemetryController {

    private final TelemetryService telemetryService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SENSOR', 'ROLE_ADMIN')")
    @Operation(summary = "Telemetri Verisi Gönder (ROLE_SENSOR veya ROLE_ADMIN)", 
               description = "Saha sensöründen gelen telemetri verisini doğrular, SHA-256 hash hesaplar ve saklar.")
    public ResponseEntity<TelemetryResponse> createTelemetry(
            @Valid @RequestBody TelemetryCreateRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        String submittedBy = authentication.getName();
        String clientIp = httpRequest.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = httpRequest.getRemoteAddr();
        }

        TelemetryResponse response = telemetryService.recordTelemetry(request, submittedBy, clientIp);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_OPERATOR', 'ROLE_ADMIN')")
    @Operation(summary = "Tüm Telemetri Verilerini Listele (ROLE_OPERATOR veya ROLE_ADMIN)", 
               description = "Sayfalı telemetri verilerini getirir.")
    public ResponseEntity<Page<TelemetryResponse>> getAllTelemetry(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "recordedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(telemetryService.getAllTelemetry(pageable));
    }

    @GetMapping("/critical")
    @PreAuthorize("hasAnyAuthority('ROLE_OPERATOR', 'ROLE_ADMIN')")
    @Operation(summary = "Kritik Seviyedeki Alarmları Listele (ROLE_OPERATOR veya ROLE_ADMIN)", 
               description = "SEVERITY=CRITICAL olan telemetri kayıtlarını filtreler.")
    public ResponseEntity<List<TelemetryResponse>> getCriticalTelemetry() {
        return ResponseEntity.ok(telemetryService.getCriticalTelemetry());
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAnyAuthority('ROLE_OPERATOR', 'ROLE_ADMIN')")
    @Operation(summary = "Son 50 Telemetri Kaydı (ROLE_OPERATOR veya ROLE_ADMIN)", 
               description = "En son kaydedilen 50 telemetri kaydını zamana göre azalan sırada döner.")
    public ResponseEntity<List<TelemetryResponse>> getRecentTelemetry() {
        return ResponseEntity.ok(telemetryService.getRecentTelemetry());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_OPERATOR', 'ROLE_ADMIN')")
    @Operation(summary = "ID ile Telemetri Detayı Getir (ROLE_OPERATOR veya ROLE_ADMIN)", 
               description = "Belirtilen ID'ye sahip telemetri verisini döner.")
    public ResponseEntity<TelemetryResponse> getTelemetryById(@PathVariable Long id) {
        return ResponseEntity.ok(telemetryService.getTelemetryById(id));
    }
}
