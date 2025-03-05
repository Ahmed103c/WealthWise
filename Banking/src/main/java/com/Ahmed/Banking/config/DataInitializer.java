package com.Ahmed.Banking.config;

import com.Ahmed.Banking.models.Category;
import com.Ahmed.repositories.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initCategories(CategoryRepository categoryRepository) {
        return args -> {
            // Vérifier si la table des catégories est vide
            if (categoryRepository.count() == 0) {
                categoryRepository.save(new Category(null, "Alimentation"));
                categoryRepository.save(new Category(null, "Loisirs"));
                categoryRepository.save(new Category(null, "Transport"));
                categoryRepository.save(new Category(null, "Logement"));
                categoryRepository.save(new Category(null, "Santé"));
                System.out.println("Catégories prédéfinies insérées.");
            }
        };
    }
}
