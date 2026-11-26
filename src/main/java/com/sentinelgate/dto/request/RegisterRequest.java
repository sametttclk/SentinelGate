package com.sentinelgate.dto.request;

import com.sentinelgate.entity.Role;
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
@Schema(description = "Yeni kullanıcı veya IoT sensör aktör kayıt istek gövdesi (Yalnızca ROLE_ADMIN)")
public class RegisterRequest {

    @NotBlank(message = "Kullanıcı adı boş bırakılamaz")
    @Size(min = 3, max = 64, message = "Kullanıcı adı 3-64 karakter arasında olmalıdır")
    @Schema(description = "Tanımlanacak kullanıcı adı veya sensör kimliği", example = "sensor-perimeter-02")
    private String username;

    @NotBlank(message = "Şifre boş bırakılamaz")
    @Size(min = 6, max = 128, message = "Şifre en az 6 karakter olmalıdır")
    @Schema(description = "Kullanıcı veya aktör şifresi", example = "SensorSecurePass2026!")
    private String password;

    @NotNull(message = "Kullanıcı rolü belirtilmelidir")
    @Schema(description = "Atanacak RBAC rolü", example = "ROLE_SENSOR")
    private Role role;
}
