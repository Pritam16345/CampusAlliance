package com.campusalliance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action; // "NOTICE_CREATED", "USER_REGISTERED", etc.

    @Column(nullable = false)
    private String performedBy; // email

    @Column(length = 500)
    private String details;

    @Column(nullable = false)
    private LocalDateTime performedAt;

    @PrePersist
    void onPersist() {
        if (performedAt == null) performedAt = LocalDateTime.now();
    }
}
