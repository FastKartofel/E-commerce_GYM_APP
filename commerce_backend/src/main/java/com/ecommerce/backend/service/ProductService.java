package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.ProductDto;
import com.ecommerce.backend.entity.CartItem;
import com.ecommerce.backend.entity.Category;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.repository.CartItemRepository;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> {
                    ProductDto dto = new ProductDto();
                    dto.setId(product.getId());
                    dto.setName(product.getName());
                    dto.setDescription(product.getDescription());
                    dto.setPrice(product.getPrice());
                    dto.setStockQuantity(product.getStockQuantity());
                    // Map reviews if needed
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product addProduct(ProductDto productDto) {
        if (productDto.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID must not be null");
        }

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + productDto.getCategoryId()));

        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setCategory(category);

        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
        productRepository.deleteById(productId);
    }

    // If using categoryName (case-insensitive)
    public List<Product> findByCategoryNameIgnoreCase(String categoryName) {
        return productRepository.findByCategoryNameIgnoreCase(categoryName);
    }

    // **New Method for Filtering by Category ID**
    public List<Product> findByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
}

