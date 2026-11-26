package com.sentinelgate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Kritik altyapı sensör telemetri veri varlığı.
 * Kriptografik SHA-256 veri bütünlüğü (payloadHash) içerir.
 */
@Entity
@Table(name = "telemetry_records", indexes = {
        @Index(name = "idx_telemetry_sensor", columnList = "sensor_id"),
        @Index(name = "idx_telemetry_zone", columnList = "facility_zone"),
        @Index(name = "idx_telemetry_severity", columnList = "severity"),
        @Index(name = "idx_telemetry_recorded_at", columnList = "recorded_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemetryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_id", nullable = false, length = 64)
    private String sensorId;

    @Column(name = "facility_zone", nullable = false, length = 64)
    private String facilityZone;

    @Column(name = "metric_type", nullable = false, length = 64)
    private String metricType;

    @Column(name = "metric_value", nullable = false)
    private Double metricValue;

    @Column(nullable = false, length = 32)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Severity severity;

    @Column(name = "payload_hash", nullable = false, length = 64)
    private String payloadHash;

    @CreationTimestamp
    @Column(name = "recorded_at", nullable = false, updatable = false)
    private LocalDateTime recordedAt;

    @Column(name = "submitted_by", nullable = false, length = 64)
    private String submittedBy;
}
