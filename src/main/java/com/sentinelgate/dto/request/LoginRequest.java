package com.sentinelgate.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Kullanıcı veya aktör kimlik doğrulama istek gövdesi")
public class LoginRequest {

    @NotBlank(message = "Kullanıcı adı boş bırakılamaz")
    @Size(min = 3, max = 64, message = "Kullanıcı adı 3-64 karakter arasında olmalıdır")
    @Schema(description = "Sistem kullanıcı adı", example = "admin")
    private String username;

    @NotBlank(message = "Şifre boş bırakılamaz")
    @Size(min = 6, max = 128, message = "Şifre en az 6 karakter olmalıdır")
    @Schema(description = "Kullanıcı şifresi", example = "Admin123!")
    private String password;
}
