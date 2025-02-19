package com.Ahmed.repositories;

import com.Ahmed.Banking.models.BudgetCategorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetCategorieRepository extends JpaRepository<BudgetCategorie, Integer> {
    Optional<BudgetCategorie> findByBudgetIdAndCategoryId(Integer budgetId, Integer categoryId);
    List<BudgetCategorie> findByBudgetId(Integer budgetId); // ✅ Ajouté

}
