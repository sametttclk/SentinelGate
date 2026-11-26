package com.sentinelgate.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {

    private Long id;
    private LocalDateTime timestamp;
    private String actorUsername;
    private String action;
    private String endpoint;
    private String httpMethod;
    private int statusCode;
    private String ipAddress;
    private String details;
}
