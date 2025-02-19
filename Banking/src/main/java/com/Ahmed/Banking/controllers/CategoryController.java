package com.Ahmed.Banking.controllers;

import com.Ahmed.Banking.models.Category;
import com.Ahmed.Banking.services.Implementations.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/predict")
    public ResponseEntity<Category> predictCategory(@RequestParam String description) {
        Category predictedCategory = categoryService.predictCategory(description);
        return ResponseEntity.ok(predictedCategory);
    }
}
