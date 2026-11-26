package com.sentinelgate.dto.request;

import com.sentinelgate.entity.Severity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Kritik altyapı sensör telemetri veri iletim istek gövdesi")
public class TelemetryCreateRequest {

    @NotBlank(message = "Sensör kimliği (sensorId) boş bırakılamaz")
    @Size(max = 64, message = "Sensör kimliği en fazla 64 karakter olabilir")
    @Schema(description = "Saha sensör tanımlayıcısı", example = "RADAR-UNIT-ALPHA")
    private String sensorId;

    @NotBlank(message = "Tesis bölgesi (facilityZone) boş bırakılamaz")
    @Size(max = 64, message = "Tesis bölgesi en fazla 64 karakter olabilir")
    @Schema(description = "Tesis / saha operasyonel bölgesi", example = "ZONE-AIR-DEFENSE-NORTH")
    private String facilityZone;

    @NotBlank(message = "Metrik türü (metricType) boş bırakılamaz")
    @Size(max = 64, message = "Metrik türü en fazla 64 karakter olabilir")
    @Schema(description = "Ölçülen telemetri metriği", example = "AZIMUTH_SCAN_SPEED")
    private String metricType;

    @NotNull(message = "Metrik sayısal değeri (metricValue) belirtilmelidir")
    @Schema(description = "Ölçülen sayısal değer", example = "78.4")
    private Double metricValue;

    @NotBlank(message = "Ölçüm birimi (unit) boş bırakılamaz")
    @Size(max = 32, message = "Birim en fazla 32 karakter olabilir")
    @Schema(description = "Fiziksel ölçüm birimi", example = "RPM")
    private String unit;

    @NotNull(message = "Kritiklik seviyesi (severity: INFO, WARNING, CRITICAL) belirtilmelidir")
    @Schema(description = "Alarm / önem derecesi", example = "INFO")
    private Severity severity;
}
