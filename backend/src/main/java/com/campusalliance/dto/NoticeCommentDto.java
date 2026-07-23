package com.campusalliance.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NoticeCommentDto {
    private Long id;
    private Long noticeId;
    private Long userId;
    private String userName;
    private String userRole;
    private String content;
    private LocalDateTime createdAt;
}
