package com.campusalliance.service;

import com.campusalliance.dto.AuditLogDto;
import com.campusalliance.entity.AuditLog;
import com.campusalliance.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(String action, String performedBy, String details) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .performedBy(performedBy)
                .details(details)
                .build();
        auditLogRepository.save(auditLog);
    }

    public List<AuditLogDto> getRecentLogs() {
        return auditLogRepository.findTop50ByOrderByPerformedAtDesc().stream()
                .map(log -> AuditLogDto.builder()
                        .id(log.getId())
                        .action(log.getAction())
                        .performedBy(log.getPerformedBy())
                        .details(log.getDetails())
                        .performedAt(log.getPerformedAt())
                        .build())
                .toList();
    }
}
