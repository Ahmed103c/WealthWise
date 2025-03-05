package com.Ahmed.Banking.services;

import com.Ahmed.Banking.models.Budget;
import com.Ahmed.Banking.models.BudgetCategorie;

import java.math.BigDecimal;
import java.util.List;

public interface BudgetService {
    Budget creerBudget(Integer utilisateurId, BigDecimal montantAlloue, String periode);
    List<Budget> getBudgetsParUtilisateur(Integer utilisateurId);
    BudgetCategorie allouerBudgetCategorie(Integer budgetId, Integer categorieId, BigDecimal montant);
    BigDecimal getBudgetRestant(Integer budgetId);
    Budget saveBudget(Budget budget);

}
