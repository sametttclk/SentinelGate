package com.sentinelgate.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sentinelgate.audit.AuditLogService;
import com.sentinelgate.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 401 Unauthorized durumlarını standart JSON formatında ele alan ve otomatik Audit Log kaydeden giriş noktası.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }

        log.warn("Kimlik doğrulaması yapılmamış erişim denemesi (401 Unauthorized) - URI: {}, IP: {}",
                request.getRequestURI(), clientIp);

        // Otomatik Audit Log Kaydı
        auditLogService.log(
                "ANONYMOUS",
                "SECURITY_UNAUTHORIZED_ACCESS",
                request.getRequestURI(),
                request.getMethod(),
                HttpStatus.UNAUTHORIZED.value(),
                clientIp,
                "Kimlik doğrulanmamış erişim girişimi: " + authException.getMessage()
        );

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Kimlik Doğrulama Gerekli")
                .message("Bu kaynağa erişmek için geçerli bir Bearer JWT token sağlamalısınız.")
                .path(request.getRequestURI())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
