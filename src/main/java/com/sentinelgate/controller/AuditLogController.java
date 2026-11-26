package com.sentinelgate.controller;

import com.sentinelgate.audit.AuditLogService;
import com.sentinelgate.dto.response.AuditLogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Denetim Kayıtları (Audit Logs)", description = "Sistem ve güvenlik ihlali denetim kayıtları (Yalnızca ROLE_ADMIN)")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @Operation(summary = "Denetim Kayıtlarını Listele", 
               description = "Filtrelenebilir (kullanıcı, aksiyon, durum kodu) ve sayfalanabilir audit log listesi.")
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogs(
            @Parameter(description = "İşlemi gerçekleştiren aktör kullanıcı adı")
            @RequestParam(required = false) String actorUsername,
            @Parameter(description = "HTTP Durum Kodu (ör. 401, 403, 200)")
            @RequestParam(required = false) Integer statusCode,
            @Parameter(description = "Aksiyon türü (ör. SECURITY_UNAUTHORIZED_ACCESS)")
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AuditLogResponse> logs = auditLogService.getAuditLogs(actorUsername, statusCode, action, pageable);
        return ResponseEntity.ok(logs);
    }
}
