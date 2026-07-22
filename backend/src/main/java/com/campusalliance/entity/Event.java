package com.campusalliance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A campus event (e.g., "Tech Fest 2026 — Main Auditorium, Aug 15").
 *
 * Structurally similar to Notice but with different fields (location,
 * eventDate). We keep them as separate entities rather than using
 * inheritance because they serve different purposes in the UI and
 * have different query patterns. Premature inheritance would just
 * make things harder to change later.
 *
 * Uses @Version for optimistic locking, same rationale as Notice.
 * No soft delete on Event — the requirement only asked for it on
 * Resource and Notice.
 */
@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @Version
    private Integer version;
}
