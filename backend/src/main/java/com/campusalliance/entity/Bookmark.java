package com.campusalliance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bookmarks", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "target_type", "target_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bookmark extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String targetType; // "NOTICE" or "RESOURCE"

    @Column(nullable = false)
    private Long targetId;
}
