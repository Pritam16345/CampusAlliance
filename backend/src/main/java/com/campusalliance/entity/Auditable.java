package com.campusalliance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Shared base class for audit fields across all entities.
 *
 * Why @MappedSuperclass instead of a regular @Entity?
 * MappedSuperclass doesn't create its own table — it just donates its
 * columns to every entity that extends it. We don't need an "auditable"
 * table, we just want every entity to have these four columns automatically.
 *
 * Spring Data JPA Auditing fills these in via @EntityListeners:
 * - createdAt / updatedAt: set automatically from the system clock
 * - createdBy / updatedBy: pulled from our AuditorAwareImpl, which reads
 *   the currently authenticated user from Spring Security context
 *
 * This replaces manual @PrePersist/@PreUpdate callbacks in every entity.
 * One place to define, every entity inherits it.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class Auditable {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @LastModifiedBy
    private String updatedBy;
}
