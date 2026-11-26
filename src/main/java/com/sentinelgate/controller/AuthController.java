package com.sentinelgate.controller;

import com.sentinelgate.dto.request.LoginRequest;
import com.sentinelgate.dto.request.RegisterRequest;
import com.sentinelgate.dto.response.AuthResponse;
import com.sentinelgate.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Kimlik Doğrulama & Yetkilendirme (Auth)", description = "Stateless JWT oturum açma ve kullanıcı kayıt uç noktaları")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Kullanıcı Girişi (Login)", description = "Kullanıcı adı ve şifre ile doğrulanıp Bearer JWT token alır.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Yeni Kullanıcı/Sensör Tanımlama (Yalnızca ROLE_ADMIN)", 
               description = "Sisteme yeni bir operatör, sensör veya yönetici kaydı oluşturur.")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
