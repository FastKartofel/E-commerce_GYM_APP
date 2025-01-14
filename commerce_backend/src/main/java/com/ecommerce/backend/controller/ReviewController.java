package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.ReviewDTO;
import com.ecommerce.backend.dto.ReviewResponseDTO;
import com.ecommerce.backend.entity.Review;
import com.ecommerce.backend.exception.ProductNotFoundException;
import com.ecommerce.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody ReviewDTO reviewDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // Retrieve username from the SecurityContext

            // Save the review with the username
            reviewService.addReview(username, reviewDTO);

            return ResponseEntity.ok("Review added successfully!");
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add review.");
        }
    }


    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByProduct(@PathVariable Long productId) {
        try {
            List<ReviewResponseDTO> reviews = reviewService.getReviewsByProduct(productId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

