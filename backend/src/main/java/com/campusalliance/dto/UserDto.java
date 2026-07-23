package com.campusalliance.dto;

import com.campusalliance.entity.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDto {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
    private boolean active;
}
