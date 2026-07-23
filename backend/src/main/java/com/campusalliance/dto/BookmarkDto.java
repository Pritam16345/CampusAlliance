package com.campusalliance.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookmarkDto {
    private Long id;
    private String targetType;
    private Long targetId;
    private String title;
    private LocalDateTime createdAt;
}
