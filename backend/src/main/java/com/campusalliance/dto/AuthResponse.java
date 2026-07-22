package com.campusalliance.dto;

import lombok.*;

// We send back the token + basic user info so the frontend can show
// the user's name and role without having to decode the JWT itself.

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private String role;
}
