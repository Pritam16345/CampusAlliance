package com.campusalliance.dto;

import lombok.*;

/** Authentication response payload containing JWT token and user profile */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private String role;
}
