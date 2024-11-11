package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Case-insensitive search by category name
    List<Product> findByCategoryNameIgnoreCase(String categoryName);

    // Alternatively, fetch by categoryId for better performance
    List<Product> findByCategoryId(Long categoryId);
}