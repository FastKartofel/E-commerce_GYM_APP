package com.ecommerce.backend.controller;

import com.ecommerce.backend.service.ProductRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final ProductRecommendationService recommendationService;

    @Autowired
    public RecommendationController(ProductRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> getRecommendations(@RequestBody Map<String, String> payload) {
        String userPrompt = payload.get("userPrompt"); // Match frontend payload
        String recommendation = recommendationService.getRecommendations(userPrompt);

        Map<String, String> response = new HashMap<>();
        response.put("recommendation", recommendation);
        return ResponseEntity.ok(response);
    }
}

