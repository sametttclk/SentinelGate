package com.sentinelgate.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Birim Testi: JwtService")
class JwtServiceTest {

    private JwtService jwtService;
    private final String secretKey = "SentinelGateSecureJwtSecretKeyForUnitTestingPurposes2026!";
    private final long expirationMs = 3600000; // 1 saat

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secretKey, expirationMs);
    }

    @Test
    @DisplayName("Başarılı JWT token üretimi ve kullanıcı adı/rol çözümlemesi")
    void shouldGenerateAndValidateTokenSuccessfully() {
        UserPrincipal principal = new UserPrincipal(
                1L,
                "operator-test",
                "passwordHash",
                com.sentinelgate.entity.Role.ROLE_OPERATOR,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_OPERATOR"))
        );
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        String token = jwtService.generateToken(auth);

        assertNotNull(token);
        assertTrue(jwtService.validateToken(token));
        assertEquals("operator-test", jwtService.extractUsername(token));
        assertEquals("ROLE_OPERATOR", jwtService.extractRole(token));
    }

    @Test
    @DisplayName("Geçersiz veya bozulmuş JWT reddedilmelidir")
    void shouldRejectInvalidToken() {
        String invalidToken = "invalid.token.signature";
        assertFalse(jwtService.validateToken(invalidToken));
    }
}
