package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.ProductDto;
import com.ecommerce.backend.entity.Category;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProduct_ValidCategory() {
        // Mock data
        ProductDto productDto = new ProductDto();
        productDto.setName("Test Product");
        productDto.setDescription("Test Description");
        productDto.setPrice(10.0);
        productDto.setStockQuantity(100);
        productDto.setCategoryId(1L);

        Category category = new Category();
        category.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute method
        Product product = productService.addProduct(productDto);

        // Assertions
        assertNotNull(product);
        assertEquals("Test Product", product.getName());
        assertEquals(category, product.getCategory());

        // Verify interactions
        verify(categoryRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testAddProduct_InvalidCategory() {
        ProductDto productDto = new ProductDto();
        productDto.setCategoryId(999L);

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Execute method and verify exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.addProduct(productDto));
        assertEquals("Category not found with ID: 999", exception.getMessage());
    }
    @Test
    void testDeleteProduct_NonexistentProduct() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Execute method and assert exception
        Exception exception = assertThrows(RuntimeException.class,
                () -> productService.deleteProduct(999L));
        assertEquals("Product not found with ID: 999", exception.getMessage());
    }
}
