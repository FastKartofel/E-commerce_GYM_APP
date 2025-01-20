package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.UserProfileUpdateDto;
import com.ecommerce.backend.service.UserService;
import com.ecommerce.backend.security.JwtUtil;
import com.ecommerce.backend.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        // Setup additional test configurations if needed
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUpdateUserProfile_Success() throws Exception {
        // Mocking the service layer behavior
        doNothing().when(userService).updateUserProfile(Mockito.anyString(), any(UserProfileUpdateDto.class));

        // Sending PUT request to update the user profile
        mockMvc.perform(put("/api/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@test.com",
                                  "shippingAddress": "123 Street",
                                  "paymentDetails": "Visa"
                                }
                                """))
                .andExpect(status().isOk());
    }
}
