// src/main/java/com/ecommerce/backend/dto/CartItemDto.java
package com.ecommerce.backend.dto;

import lombok.Data;

@Data
public class CartItemDto {
    private Long productId;
    private Integer quantity;
}
