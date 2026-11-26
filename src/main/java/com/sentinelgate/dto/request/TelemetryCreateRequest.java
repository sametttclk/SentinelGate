package com.sentinelgate.dto.request;

import com.sentinelgate.entity.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemetryCreateRequest {

    @NotBlank(message = "Sensör kimliği (sensorId) boş bırakılamaz")
    @Size(max = 64, message = "Sensör kimliği en fazla 64 karakter olabilir")
    private String sensorId;

    @NotBlank(message = "Tesis bölgesi (facilityZone) boş bırakılamaz")
    @Size(max = 64, message = "Tesis bölgesi en fazla 64 karakter olabilir")
    private String facilityZone;

    @NotBlank(message = "Metrik türü (metricType) boş bırakılamaz")
    @Size(max = 64, message = "Metrik türü en fazla 64 karakter olabilir")
    private String metricType;

    @NotNull(message = "Metrik sayısal değeri (metricValue) belirtilmelidir")
    private Double metricValue;

    @NotBlank(message = "Ölçüm birimi (unit) boş bırakılamaz")
    @Size(max = 32, message = "Birim en fazla 32 karakter olabilir")
    private String unit;

    @NotNull(message = "Kritiklik seviyesi (severity: INFO, WARNING, CRITICAL) belirtilmelidir")
    private Severity severity;
}
