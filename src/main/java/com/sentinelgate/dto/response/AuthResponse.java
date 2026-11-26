package com.sentinelgate.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Kimlik doğrulama ve JWT yanıt nesnesi")
public class AuthResponse {

    @Schema(description = "İmzalanmış Stateless JWT Bearer Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Builder.Default
    @Schema(description = "Token yetkilendirme tipi", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Token geçerlilik süresi (saniye cinsinden)", example = "86400")
    private Long expiresIn;

    @Schema(description = "Doğrulanan kullanıcı adı", example = "admin")
    private String username;

    @Schema(description = "Kullanıcıya atanmış RBAC rolü", example = "ROLE_ADMIN")
    private String role;
}
