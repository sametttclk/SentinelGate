package com.sentinelgate.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Sistem ve güvenlik denetim kaydı yanıt nesnesi")
public class AuditLogResponse {

    @Schema(description = "Kayıt ID", example = "1")
    private Long id;

    @Schema(description = "İşlem zamanı", example = "2026-11-26T19:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "İşlemi gerçekleştiren aktör", example = "operator")
    private String actorUsername;

    @Schema(description = "Denetim eylem kodu", example = "SECURITY_ACCESS_DENIED")
    private String action;

    @Schema(description = "Erişilen uç nokta", example = "/api/v1/telemetry")
    private String endpoint;

    @Schema(description = "HTTP Metodu", example = "POST")
    private String httpMethod;

    @Schema(description = "HTTP Durum Kodu", example = "403")
    private int statusCode;

    @Schema(description = "İstemci IP Adresi", example = "192.168.1.105")
    private String ipAddress;

    @Schema(description = "Ayrıntılı denetim mesajı", example = "Yetkisiz rol ile işlem teşebbüsü")
    private String details;
}
