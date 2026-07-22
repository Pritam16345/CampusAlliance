package com.campusalliance.service;

import com.campusalliance.dto.AuthResponse;
import com.campusalliance.dto.RegisterRequest;
import com.campusalliance.entity.Role;
import com.campusalliance.entity.User;
import com.campusalliance.repository.UserRepository;
import com.campusalliance.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtils jwtUtils;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest("Alice", "alice@test.com", "password123", "STUDENT", null);
        
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed-pwd");
        when(jwtUtils.generateToken(request.getEmail(), "STUDENT")).thenReturn("mock-jwt-token");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("Alice", response.getFullName());
        
        // verify save was called once
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        // Arrange
        RegisterRequest request = new RegisterRequest("Bob", "bob@test.com", "pwd", "STUDENT", null);
        when(userRepository.existsByEmail("bob@test.com")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> authService.register(request));
            
        assertEquals("Email already registered", ex.getMessage());
        verify(userRepository, never()).save(any(User.class)); // shouldn't try to save
    }
}
