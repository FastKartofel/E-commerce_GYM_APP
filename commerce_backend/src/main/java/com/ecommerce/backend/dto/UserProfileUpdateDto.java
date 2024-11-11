package com.ecommerce.backend.dto;

import lombok.Data;

@Data
public class UserProfileUpdateDto {
    //private String username;
    private String email;
    private String shippingAddress;
    private String paymentDetails;

    //private String role;
}

