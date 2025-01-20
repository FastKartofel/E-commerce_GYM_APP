package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.ProductDto;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProductsByCategory() {
        List<Product> mockProducts = Collections.singletonList(new Product());
        when(productService.findByCategoryId(1L)).thenReturn(mockProducts);

        ResponseEntity<List<Product>> response = productController.getProductsByCategory(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockProducts, response.getBody());
    }

    @Test
    void testAddProduct() {
        ProductDto productDto = new ProductDto();
        Product mockProduct = new Product();
        when(productService.addProduct(productDto)).thenReturn(mockProduct);

        ResponseEntity<Product> response = productController.addProduct(productDto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(mockProduct, response.getBody());
    }

    @Test
    void testDeleteProduct() {
        ResponseEntity<Void> response = productController.deleteProduct(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void testAddProduct_InvalidData() {
        // Mock invalid product DTO
        ProductDto productDto = new ProductDto(); // Missing required fields

        // Mock behavior
        when(productService.addProduct(productDto)).thenThrow(new IllegalArgumentException("Invalid product data"));

        // Execute method and assert exception
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> productController.addProduct(productDto));
        assertEquals("Invalid product data", exception.getMessage());
    }

}
