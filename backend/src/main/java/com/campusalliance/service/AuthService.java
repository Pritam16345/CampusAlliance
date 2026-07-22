package com.campusalliance.service;

import com.campusalliance.dto.AuthResponse;
import com.campusalliance.dto.LoginRequest;
import com.campusalliance.dto.RegisterRequest;
import com.campusalliance.entity.Role;
import com.campusalliance.entity.User;
import com.campusalliance.repository.UserRepository;
import com.campusalliance.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // parse role string to enum, throw if invalid
        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role. Must be STUDENT, FACULTY, or ADMIN");
        }

        // Validate Admin Secret Key if attempting to register as ADMIN
        if (role == Role.ADMIN) {
            String expectedKey = "campus-admin-2026";
            if (request.getAdminSecretKey() == null || !request.getAdminSecretKey().equals(expectedKey)) {
                throw new IllegalArgumentException("Invalid Admin Secret Key. You are not authorized to create an Admin account.");
            }
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();
        userRepository.save(user);

        // generate token so user is logged in right after registering
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        // Spring's AuthenticationManager checks email+password against the DB.
        // Throws AuthenticationException if wrong — our global handler catches that.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }
}
