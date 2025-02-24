package com.Ahmed.Banking.controllers;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/chatbot")
public class ChatBotController {

    private final RestTemplate restTemplate;
    private final String FASTAPI_URL = "http://localhost:8001/ask/{userId}";

    public ChatBotController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/ask/{userId}")
    public String askChatBot(@PathVariable Long userId, @RequestParam String question) {
        if (question == null || question.trim().isEmpty()) {
            return "Aucune question fournie.";
        }

        // Construire le corps de la requête pour FastAPI
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("question", question);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // Envoyer une requête POST à FastAPI
            ResponseEntity<Map> response = restTemplate.exchange(FASTAPI_URL, HttpMethod.POST, requestEntity, Map.class, userId);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (String) response.getBody().get("answer");
            }
        } catch (Exception e) {
            System.err.println("🚨 Erreur lors de la communication avec FastAPI : " + e.getMessage());
        }

        return "Erreur dans la communication avec le chatbot.";
    }
}

