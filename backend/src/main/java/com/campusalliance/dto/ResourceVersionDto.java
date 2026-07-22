package com.campusalliance.dto;

import lombok.*;

import java.time.LocalDateTime;

// Version metadata — again no file bytes, just info the UI needs
// to show a version list with download links.

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
