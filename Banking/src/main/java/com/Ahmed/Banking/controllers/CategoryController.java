package com.Ahmed.Banking.controllers;

import com.Ahmed.Banking.dto.PredictionRequest;
import com.Ahmed.Banking.models.Category;
import com.Ahmed.Banking.services.Implementations.CategoryService;
import com.Ahmed.repositories.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/predictions")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryService categoryService, CategoryRepository categoryRepository) {
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }

    // Endpoint pour obtenir la catégorie prédite (via POST avec JSON)
    @PostMapping("/save")
    public ResponseEntity<String> savePrediction(@RequestBody PredictionRequest request) {
        // Ici vous pouvez enregistrer ou logger la prédiction si nécessaire
        System.out.println("Description: " + request.getDescription());
        System.out.println("Catégorie prédite: " + request.getPredictedCategory());
        return ResponseEntity.ok("Prédiction reçue avec succès !");
    }

    // Vous pouvez également conserver un endpoint de prédiction
    // si besoin, par exemple pour tester en interne
    @PostMapping("/predict")
    public ResponseEntity<Category> predictCategory(@RequestBody PredictionRequest request) {
        Category predictedCategory = categoryService.predictCategory(request.getDescription());
        return ResponseEntity.ok(predictedCategory);
    }
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> all = categoryRepository.findAll();
        return ResponseEntity.ok(all);
    }


}
