package com.sentinelgate.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standart hata yanıt nesnesi")
public class ApiErrorResponse {

    @Schema(description = "Hata oluşma zamanı", example = "2026-11-26T19:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP Durum Kodu", example = "403")
    private int status;

    @Schema(description = "Hata Başlığı", example = "Yetki Dışı Erişim")
    private String error;

    @Schema(description = "Hata Açıklaması", example = "Bu işlemi gerçekleştirmek için yetkiniz yetersizdir.")
    private String message;

    @Schema(description = "Hatanın meydana geldiği URI", example = "/api/v1/telemetry")
    private String path;

    @Schema(description = "Varsa detaylı validasyon hataları")
    private Map<String, String> validationErrors;
}
