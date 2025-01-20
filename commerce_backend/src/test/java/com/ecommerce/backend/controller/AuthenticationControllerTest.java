package com.ecommerce.backend.controller;

import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.security.JwtUtil;
import com.ecommerce.backend.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAuthenticationToken() {
        // Mock input user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        // Mock authentication result
        Authentication authentication = mock(Authentication.class);

        // Mock behaviors
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "testuser", "password", Collections.emptyList()
        );
        when(userDetailsService.loadUserByUsername("testuser"))
                .thenReturn(userDetails);

        when(jwtUtil.generateToken("testuser", Collections.emptyList()))
                .thenReturn("test-token");

        // Execute the method
        ResponseEntity<?> response = authenticationController.createAuthenticationToken(user);

        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken("testuser", Collections.emptyList());

        // Assert the response
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("test-token", response.getBody());
    }

    @Test
    void testCreateAuthenticationToken_InvalidCredentials() {
        // Mock input user
        User user = new User();
        user.setUsername("invalidUser");
        user.setPassword("wrongPassword");

        // Mock authentication exception
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Execute the method and assert exception
        Exception exception = assertThrows(RuntimeException.class,
                () -> authenticationController.createAuthenticationToken(user));
        assertEquals("Invalid credentials", exception.getMessage());
    }

}
