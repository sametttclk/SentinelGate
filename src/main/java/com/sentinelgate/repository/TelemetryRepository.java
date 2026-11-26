package com.sentinelgate.repository;

import com.sentinelgate.entity.Severity;
import com.sentinelgate.entity.TelemetryRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelemetryRepository extends JpaRepository<TelemetryRecord, Long> {

    List<TelemetryRecord> findBySeverity(Severity severity);

    List<TelemetryRecord> findBySensorId(String sensorId);

    List<TelemetryRecord> findByFacilityZone(String facilityZone);

    List<TelemetryRecord> findTop50ByOrderByRecordedAtDesc();

    Page<TelemetryRecord> findAll(Pageable pageable);
}
