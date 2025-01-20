package com.ecommerce.backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void testPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder);
    }

    @Test
    void testPasswordEncoderProducesValidHash() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String rawPassword = "password123";
        String encodedPassword = encoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
    }

}
