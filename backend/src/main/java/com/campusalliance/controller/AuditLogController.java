package com.campusalliance.controller;

import com.campusalliance.dto.AuditLogDto;
import com.campusalliance.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogDto>> getRecentLogs() {
        return ResponseEntity.ok(auditLogService.getRecentLogs());
    }
}
