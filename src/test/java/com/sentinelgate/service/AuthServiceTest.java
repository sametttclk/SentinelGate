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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Birim Testi: AuthService")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Doğru kimlik bilgileri ile giriş yapıldığında AuthResponse dönmelidir")
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("admin", "Admin123!");
        UserPrincipal principal = new UserPrincipal(
                1L, "admin", "encodedPass", Role.ROLE_ADMIN, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtService.generateToken(auth)).thenReturn("mocked.jwt.token");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("admin", response.getUsername());
        assertEquals("ROLE_ADMIN", response.getRole());
        assertEquals("mocked.jwt.token", response.getToken());
    }

    @Test
    @DisplayName("Hatalı şifrede BadCredentialsException fırlatılmalıdır")
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        LoginRequest request = new LoginRequest("admin", "WrongPass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Hatalı şifre"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Kullanıcı adı daha önce kayıtlıysa BadRequestException fırlatılmalıdır")
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest("existingUser", "Pass123!", Role.ROLE_OPERATOR);
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }
}
