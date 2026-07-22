package com.campusalliance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Join table: records that a specific user has seen a specific notice.
 *
 * This powers the "72% of students have seen this" stat for faculty/admins.
 *
 * Why a dedicated entity instead of a plain @ManyToMany join table?
 * We need the seenAt timestamp to know *when* the user saw it, and
 * @ManyToMany join tables can't hold extra columns. A dedicated entity
 * with its own fields gives us that flexibility.
 *
 * The unique constraint on (notice, user) prevents duplicate records
 * if a user views the same notice multiple times — only the first
 * view is recorded.
 */
@Entity
@Table(name = "notice_seen_by",
       uniqueConstraints = @UniqueConstraint(
               columnNames = {"notice_id", "user_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeSeenBy extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * When the user first viewed the notice. We store this explicitly
     * (in addition to Auditable.createdAt) because it's a business field
     * that the API returns, not just an audit trail.
     */
    @Column(nullable = false)
    private LocalDateTime seenAt;

    @PrePersist
    protected void onSeen() {
        if (this.seenAt == null) {
            this.seenAt = LocalDateTime.now();
        }
    }
}
