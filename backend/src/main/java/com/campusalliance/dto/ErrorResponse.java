package com.campusalliance.dto;

import lombok.*;

import java.time.LocalDateTime;

// Standard error shape so the frontend always knows what to expect.
// Every error response from the API looks like this.

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}
