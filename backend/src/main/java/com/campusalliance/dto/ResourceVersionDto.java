package com.campusalliance.dto;

import lombok.*;

import java.time.LocalDateTime;

/** DTO representing a specific version of an academic resource */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResourceVersionDto {
    private Long id;
    private Integer versionNumber;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private String uploadedByName;
    private LocalDateTime createdAt;
}
