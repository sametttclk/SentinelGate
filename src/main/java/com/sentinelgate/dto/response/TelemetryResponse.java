package com.sentinelgate.dto.response;

import com.sentinelgate.entity.Severity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Telemetri veri yanıt nesnesi")
public class TelemetryResponse {

    @Schema(description = "Kayıt veritabanı ID'si", example = "1")
    private Long id;

    @Schema(description = "Sensör kimliği", example = "RADAR-UNIT-ALPHA")
    private String sensorId;

    @Schema(description = "Tesis bölgesi", example = "ZONE-AIR-DEFENSE-NORTH")
    private String facilityZone;

    @Schema(description = "Metrik türü", example = "AZIMUTH_SCAN_SPEED")
    private String metricType;

    @Schema(description = "Ölçülen değer", example = "72.5")
    private Double metricValue;

    @Schema(description = "Ölçüm birimi", example = "RPM")
    private String unit;

    @Schema(description = "Kritiklik seviyesi", example = "INFO")
    private Severity severity;

    @Schema(description = "Kriptografik SHA-256 veri bütünlük özeti", example = "c4ca4238a0b923820dcc509a6f75849b28230747805067756543210abcdef001")
    private String payloadHash;

    @Schema(description = "Kayıt zaman damgası", example = "2026-11-26T19:30:00")
    private LocalDateTime recordedAt;

    @Schema(description = "Veriyi sisteme ileten kullanıcı/aktör", example = "sensor-01")
    private String submittedBy;
}
