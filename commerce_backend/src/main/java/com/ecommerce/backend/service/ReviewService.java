package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.ReviewDTO;
import com.ecommerce.backend.dto.ReviewResponseDTO;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.Review;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.ProductNotFoundException;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.ReviewRepository;
import com.ecommerce.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * Adds a review for a product.
     *
     * @param username the username of the user adding the review
     * @param review   the review object containing content, rating, and product ID
     * @return the saved Review object
     */
    public Review addReview(String username, ReviewDTO reviewDTO) throws ProductNotFoundException {
        // Find the user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Find the product by product ID
        Product product = productRepository.findById(reviewDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + reviewDTO.getProductId()));

        // Create a new Review entity and set its fields
        Review review = new Review();
        review.setContent(reviewDTO.getContent());
        review.setRating(reviewDTO.getRating());
        review.setUser(user);
        review.setProduct(product);
        review.setUsername(username);

        // Save the review
        return reviewRepository.save(review);
    }



    /**
     * Fetches all reviews for a given product.
     *
     * @param productId the ID of the product
     * @return a list of reviews for the product
     */
    public List<ReviewResponseDTO> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductId(productId).stream().map(review -> {
            ReviewResponseDTO dto = new ReviewResponseDTO();
            dto.setContent(review.getContent());
            dto.setRating(review.getRating());
            dto.setUsername(review.getUser().getUsername()); // Fetch the username
            dto.setCreatedAt(review.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }
}

