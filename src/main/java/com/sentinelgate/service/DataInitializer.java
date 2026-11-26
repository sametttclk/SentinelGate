package com.sentinelgate.service;

import com.sentinelgate.audit.AuditLogService;
import com.sentinelgate.entity.Role;
import com.sentinelgate.entity.Severity;
import com.sentinelgate.entity.TelemetryRecord;
import com.sentinelgate.entity.User;
import com.sentinelgate.repository.TelemetryRepository;
import com.sentinelgate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Uygulama açılışında demo kullanıcıları ve başlangıç telemetri kayıtlarını
 * idempotent (tekrar çalıştığında mükerrer kayıt oluşturmayan) şekilde yükler.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TelemetryRepository telemetryRepository;
    private final AuditLogService auditLogService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("SentinelGate Veri İlklendirme (Data Initializer) kontrolü başlatılıyor...");

        seedUsers();
        seedTelemetryData();

        log.info("SentinelGate Veri İlklendirme tamamlandı. Sistem göreve hazır.");
    }

    private void seedUsers() {
        seedUserIfNotExists("admin", "Admin123!", Role.ROLE_ADMIN);
        seedUserIfNotExists("operator", "Oper123!", Role.ROLE_OPERATOR);
        seedUserIfNotExists("sensor-01", "Sensor123!", Role.ROLE_SENSOR);
    }

    private void seedUserIfNotExists(String username, String rawPassword, Role role) {
        if (!userRepository.existsByUsername(username)) {
            User user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(rawPassword))
                    .role(role)
                    .enabled(true)
                    .build();
            userRepository.save(user);
            log.info("Demo kullanıcı başarıyla oluşturuldu: {} ({})", username, role);

            auditLogService.log(
                    "SYSTEM_INIT",
                    "USER_SEED_CREATED",
                    "/data-initializer",
                    "SYSTEM",
                    200,
                    "127.0.0.1",
                    "Kullanıcı oluşturuldu: " + username + " Rol: " + role
            );
        } else {
            log.debug("Kullanıcı zaten mevcut, atlandı: {}", username);
        }
    }

    private void seedTelemetryData() {
        if (telemetryRepository.count() == 0) {
            log.info("Başlangıç telemetri test verileri oluşturuluyor...");

            LocalDateTime now = LocalDateTime.now();

            List<TelemetryRecord> initialRecords = List.of(
                    TelemetryRecord.builder()
                            .sensorId("RADAR-UNIT-ALPHA")
                            .facilityZone("ZONE-AIR-DEFENSE-NORTH")
                            .metricType("AZIMUTH_SCAN_SPEED")
                            .metricValue(72.5)
                            .unit("RPM")
                            .severity(Severity.INFO)
                            .payloadHash("c4ca4238a0b923820dcc509a6f75849b28230747805067756543210abcdef001")
                            .recordedAt(now.minusMinutes(15))
                            .submittedBy("sensor-01")
                            .build(),

                    TelemetryRecord.builder()
                            .sensorId("POWER-GRID-MAIN")
                            .facilityZone("ZONE-ENERGY-SUBSTATION-B")
                            .metricType("TRANSFORMER_TEMPERATURE")
                            .metricValue(68.4)
                            .unit("CELSIUS")
                            .severity(Severity.WARNING)
                            .payloadHash("c4ca4238a0b923820dcc509a6f75849b28230747805067756543210abcdef002")
                            .recordedAt(now.minusMinutes(10))
                            .submittedBy("sensor-01")
                            .build(),

                    TelemetryRecord.builder()
                            .sensorId("PERIMETER-SENSOR-04")
                            .facilityZone("ZONE-BORDER-OUTPOST-CHARLIE")
                            .metricType("VIBRATION_SEISMIC")
                            .metricValue(8.9)
                            .unit("MM_PER_SEC")
                            .severity(Severity.CRITICAL)
                            .payloadHash("c4ca4238a0b923820dcc509a6f75849b28230747805067756543210abcdef003")
                            .recordedAt(now.minusMinutes(5))
                            .submittedBy("sensor-01")
                            .build(),

                    TelemetryRecord.builder()
                            .sensorId("REACTOR-COOLANT-01")
                            .facilityZone("ZONE-PRIMARY-CONTAINMENT")
                            .metricType("PRESSURE_BAR")
                            .metricValue(154.2)
                            .unit("BAR")
                            .severity(Severity.INFO)
                            .payloadHash("c4ca4238a0b923820dcc509a6f75849b28230747805067756543210abcdef004")
                            .recordedAt(now.minusMinutes(2))
                            .submittedBy("sensor-01")
                            .build()
            );

            telemetryRepository.saveAll(initialRecords);
            log.info("{} adet başlangıç telemetri verisi başarıyla eklendi.", initialRecords.size());

            auditLogService.log(
                    "SYSTEM_INIT",
                    "TELEMETRY_SEED_CREATED",
                    "/data-initializer",
                    "SYSTEM",
                    200,
                    "127.0.0.1",
                    initialRecords.size() + " adet başlangıç telemetri kaydı yüklendi."
            );
        } else {
            log.debug("Telemetri verisi veritabanında zaten mevcut, ilklendirme atlandı.");
        }
    }
}
