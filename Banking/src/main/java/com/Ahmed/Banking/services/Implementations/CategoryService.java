package com.Ahmed.Banking.services.Implementations;

import com.Ahmed.Banking.models.Category;
import com.Ahmed.repositories.CategoryRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CategoryService {

    private static final String FASTAPI_URL = "http://localhost:8000/predict"; // URL du modèle ML
    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate;

    public CategoryService(CategoryRepository categoryRepository, RestTemplate restTemplate) {
        this.categoryRepository = categoryRepository;
        this.restTemplate = restTemplate;
    }

    // ✅ Classification automatique via FastAPI
    public Category predictCategory(String description) {
        if (description == null || description.trim().isEmpty()) {
            return new Category(0, "Inconnue");
        }

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("description", description);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(FASTAPI_URL, HttpMethod.POST, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String predictedCategoryName = (String) response.getBody().get("predicted_category");

                // Vérifier si la catégorie existe déjà, sinon l'ajouter
                return categoryRepository.findByName(predictedCategoryName)
                        .orElseGet(() -> categoryRepository.save(new Category(null, predictedCategoryName)));
            }
        } catch (Exception e) {
            System.err.println("🚨 Erreur lors de la communication avec FastAPI : " + e.getMessage());
        }

        return new Category(0, "Inconnue");
    }
}
