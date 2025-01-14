package com.ecommerce.backend.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private String content;
    private int rating;
    private Long productId;
}


