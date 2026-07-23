package com.campusalliance.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLogDto {
    private Long id;
    private String action;
    private String performedBy;
    private String details;
    private LocalDateTime performedAt;
}
