package com.campusalliance.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NoticeDto {
    private Long id;
    private String title;
    private String content;
    private String postedByName;
    private Integer version; // sent back to client for optimistic locking
    private long seenCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
