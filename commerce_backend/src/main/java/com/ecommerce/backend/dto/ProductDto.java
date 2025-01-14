package com.ecommerce.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDto {

    private Long id;
    private String name;
    private String description;
    private double price;
    private int stockQuantity;
    private Long categoryId;
    private List<ReviewDTO> reviews;

}