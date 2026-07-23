package com.campusalliance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notice_comments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NoticeComment extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 2000)
    private String content;
}
