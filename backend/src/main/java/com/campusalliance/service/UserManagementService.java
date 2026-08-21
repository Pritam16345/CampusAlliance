package com.campusalliance.service;

import com.campusalliance.dto.UserDto;
import com.campusalliance.entity.User;
import com.campusalliance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public UserDto toggleUserStatus(Long userId, String currentAdminEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getEmail().equalsIgnoreCase(currentAdminEmail) && Boolean.TRUE.equals(user.getActive())) {
            throw new IllegalArgumentException("You cannot suspend your own active administrator account.");
        }

        user.setActive(!Boolean.TRUE.equals(user.getActive()));
        userRepository.save(user);

        String action = Boolean.TRUE.equals(user.getActive()) ? "USER_ACTIVATED" : "USER_SUSPENDED";
        auditLogService.log(action, currentAdminEmail, "Target User: " + user.getEmail() + " (" + user.getRole().name() + ")");

        return toDto(user);
    }

    public Map<String, Long> getUserStats() {
        return userRepository.findAll().stream()
                .collect(Collectors.groupingBy(u -> u.getRole().name(), Collectors.counting()));
    }

    private UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .role(u.getRole())
                .createdAt(u.getCreatedAt())
                .active(u.getActive())
                .build();
    }
}
