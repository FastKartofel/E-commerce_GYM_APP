package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.ProductDto;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    /////

    private final ProductService productService;
    private final ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    public ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    /**
     * Fetch all products.
     *
     * @return List of all products.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        logger.info("Fetching all products");
        List<Product> products = productRepository.findAll();
        logger.info("Total number of products fetched: {}", products.size());

        if (products.isEmpty()) {
            logger.warn("No products available in the database");
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        }

        return ResponseEntity.ok(products); // HTTP 200 OK
    }

    /**
     * Fetch products by categoryId.
     *
     * @param categoryId Category ID to filter products.
     * @return List of products in the specified category.
     */
    @GetMapping("/by-category")
    public ResponseEntity<List<Product>> getProductsByCategory(@RequestParam Long categoryId) {
        logger.info("Fetching products for categoryId: {}", categoryId);
        List<Product> products = productService.findByCategoryId(categoryId);
        logger.info("Number of products fetched: {}", products.size());

        if (products.isEmpty()) {
            logger.warn("No products found for categoryId: {}", categoryId);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        }

        return ResponseEntity.ok(products); // HTTP 200 OK
    }

    /**
     * Get product by ID.
     *
     * @param id Product ID.
     * @return Product details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Add a new product. Accessible by ADMIN.
     *
     * @param productDto Product data transfer object.
     * @return Created product.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> addProduct(@RequestBody ProductDto productDto) {
        Product newProduct = productService.addProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    /**
     * Delete a product by ID. Accessible by ADMIN.
     *
     * @param id Product ID.
     * @return No content.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
