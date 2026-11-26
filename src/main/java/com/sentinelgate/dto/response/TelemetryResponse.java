package com.sentinelgate.dto.response;

import com.sentinelgate.entity.Severity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemetryResponse {

    private Long id;
    private String sensorId;
    private String facilityZone;
    private String metricType;
    private Double metricValue;
    private String unit;
    private Severity severity;
    private String payloadHash;
    private LocalDateTime recordedAt;
    private String submittedBy;
}
