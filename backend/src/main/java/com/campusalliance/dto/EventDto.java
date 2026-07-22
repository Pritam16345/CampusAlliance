package com.campusalliance.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventDto {
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime eventDate;
    private String organizerName;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
