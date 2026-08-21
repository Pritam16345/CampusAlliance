package com.campusalliance.dto;

import lombok.*;

import java.time.LocalDateTime;

/** Standard API error response */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}
