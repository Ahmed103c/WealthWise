package com.Ahmed.repositories;



import com.Ahmed.Banking.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {  // ✅ Use Integer, not Long
}
