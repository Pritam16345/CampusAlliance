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

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public UserDto toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(!user.getActive());
        userRepository.save(user);
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
