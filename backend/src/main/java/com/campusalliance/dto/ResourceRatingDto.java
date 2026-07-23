package com.campusalliance.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResourceRatingDto {
    private Long id;
    private Long resourceId;
    private Long userId;
    private String userName;
    private Integer rating;
    private LocalDateTime createdAt;
}
