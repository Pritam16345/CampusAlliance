package com.campusalliance.dto;

import lombok.*;

import java.time.LocalDateTime;

// Why a DTO? We don't want to send the full entity (file bytes, lazy proxies,
// internal fields). This is just the metadata the frontend needs to display.

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResourceDto {
    private Long id;
    private String title;
    private String description;
    private String courseName;
    private String uploadedByName;
    private int totalVersions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
