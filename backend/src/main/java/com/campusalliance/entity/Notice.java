package com.campusalliance.entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.ArrayList;
import java.util.List;

/**
 * A campus-wide notice (e.g., "Library closed this Saturday").
 *
 * Uses @Version for optimistic locking — if two admins edit the same notice
 * at the same time, the second save fails with OptimisticLockException
 * instead of silently overwriting the first edit. The frontend can then
 * tell the user to refresh and retry.
 */
@Entity
@Table(name = "notices")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;

    /**
     * Optimistic lock version — JPA auto-increments on every update.
     * The client must send this value back on updates so JPA can detect
     * conflicts. Named "version" which maps to a "version" DB column.
     */
    @Version
    private Integer version;


    /**
     * "Seen by" tracking — which users have viewed this notice.
     * We read this to calculate "72% of students have seen this".
     */
    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NoticeSeenBy> seenByRecords = new ArrayList<>();
}
