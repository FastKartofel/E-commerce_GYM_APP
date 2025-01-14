package com.ecommerce.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponseDTO {
    private String content;
    private int rating;
    private String username; // Include the username explicitly
    private LocalDateTime createdAt; // Optional, for displaying review time
}

