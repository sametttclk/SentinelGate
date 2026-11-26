package com.sentinelgate.service;

import com.sentinelgate.dto.request.LoginRequest;
import com.sentinelgate.dto.request.RegisterRequest;
import com.sentinelgate.dto.response.AuthResponse;
import com.sentinelgate.entity.Role;
import com.sentinelgate.entity.User;
import com.sentinelgate.exception.BadRequestException;
import com.sentinelgate.repository.UserRepository;
import com.sentinelgate.security.JwtService;
import com.sentinelgate.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Kullanıcı girişi ve JWT token üretimi.
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Kullanıcı giriş talebi: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(authentication);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationMs() / 1000)
                .username(principal.getUsername())
                .role(principal.getRole().name())
                .build();
    }

    /**
     * Yeni kullanıcı/sensör kaydı (Yalnızca ROLE_ADMIN yetkisindeki kullanıcılar çağırabilir).
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Yeni kullanıcı/aktör kayıt talebi: {}, Rol: {}", request.getUsername(), request.getRole());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Bu kullanıcı adı zaten sistemde kayıtlı: " + request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser.getUsername(), savedUser.getRole().name(), savedUser.getId());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationMs() / 1000)
                .username(savedUser.getUsername())
                .role(savedUser.getRole().name())
                .build();
    }
}
