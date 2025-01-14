package com.ecommerce.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ProductRecommendationService {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private final String apiKey;

    public ProductRecommendationService(@Value("${openai.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    public String getRecommendations(String userPrompt) {
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");

            // Create the request body
            String requestBody = String.format("""
                {
                    "model": "gpt-3.5-turbo",
                    "messages": [{"role": "user", "content": "%s"}],
                    "max_tokens": 50
                }
                """, userPrompt);

            RequestBody body = RequestBody.create(requestBody, mediaType);
            Request request = new Request.Builder()
                    .url(OPENAI_API_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                return "Error: OpenAI API returned " + response.message();
            }

            // Parse the response JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseBody = mapper.readTree(response.body().string());

            // Extract the assistant's message content
            String recommendation = responseBody
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            return recommendation;

        } catch (IOException e) {
            e.printStackTrace();
            return "Error fetching recommendations. Please try again later.";
        }
    }
}

