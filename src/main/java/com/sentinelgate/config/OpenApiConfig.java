package com.sentinelgate.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SentinelGate - Kritik Altyapı İzleme ve Güvenli Mesajlaşma API",
                version = "1.0.0",
                description = "Savunma Sanayi ve Kritik Milli Altyapı Güvenliği Standartlarına Uygun Stateless JWT & RBAC Destekli Backend API",
                contact = @Contact(
                        name = "SentinelGate Sistem Mimari Ekibi",
                        email = "devops@sentinelgate.mil.internal"
                ),
                license = @License(
                        name = "Proprietary Defense-Grade License"
                )
        )
)
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Stateless JWT Bearer Token ile yetkilendirme. Örnek: 'Bearer eyJhbGciOi...'"
)
public class OpenApiConfig {
}
