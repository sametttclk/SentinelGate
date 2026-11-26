package com.sentinelgate.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Kullanıcı adı boş bırakılamaz")
    @Size(min = 3, max = 64, message = "Kullanıcı adı 3-64 karakter arasında olmalıdır")
    private String username;

    @NotBlank(message = "Şifre boş bırakılamaz")
    @Size(min = 6, max = 128, message = "Şifre en az 6 karakter olmalıdır")
    private String password;
}
