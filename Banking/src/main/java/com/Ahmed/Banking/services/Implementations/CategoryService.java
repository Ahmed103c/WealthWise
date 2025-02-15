package com.Ahmed.Banking.services.Implementations;


import com.Ahmed.Banking.models.Category;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    public Category predictCategory(String description) {
        // Implement logic to predict category based on description
        // This might involve machine learning or simple rule-based logic
        return new Category(); // Placeholder for actual prediction logic
    }
}
