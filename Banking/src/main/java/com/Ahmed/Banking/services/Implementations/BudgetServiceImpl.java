package com.Ahmed.Banking.services.Implementations;

import com.Ahmed.Banking.models.*;
import com.Ahmed.Banking.services.BudgetService;
import com.Ahmed.Banking.services.NotificationService;

import com.Ahmed.repositories.BudgetCategorieRepository;
import com.Ahmed.repositories.BudgetRepository;
import com.Ahmed.repositories.CategoryRepository;
import com.Ahmed.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


import com.Ahmed.Banking.models.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetCategorieRepository budgetCategorieRepository;
    private final CategoryRepository categoryRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;

    public BudgetServiceImpl(BudgetRepository budgetRepository,
                             BudgetCategorieRepository budgetCategorieRepository,
                             CategoryRepository categoryRepository,
                             UtilisateurRepository utilisateurRepository,
                             NotificationService notificationService) {
        this.budgetRepository = budgetRepository;
        this.budgetCategorieRepository = budgetCategorieRepository;
        this.categoryRepository = categoryRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Budget creerBudget(Integer utilisateurId, BigDecimal montantAlloue, String periode) {
        // 🔥 Vérifier si l'utilisateur existe
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("❌ Utilisateur introuvable !"));

        LocalDate startDate, endDate;
        switch (periode.toLowerCase()) {
            case "mensuel":
                startDate = LocalDate.now().withDayOfMonth(1);
                endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
                break;
            case "hebdomadaire":
                startDate = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
                endDate = startDate.plusDays(6);
                break;
            case "annuel":
                startDate = LocalDate.now().withDayOfYear(1);
                endDate = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
                break;
            default:
                throw new IllegalArgumentException("❌ Période invalide !");
        }

        // 🔥 Vérifier si un budget pour cette période existe déjà
        Optional<Budget> existingBudget = budgetRepository.findByUtilisateur(utilisateur)
                .stream()
                .filter(b -> b.getStartDate().equals(startDate) && b.getEndDate().equals(endDate))
                .findFirst();

        if (existingBudget.isPresent()) {
            throw new RuntimeException("⚠️ Un budget pour cette période existe déjà !");
        }

        // ✅ Création du budget avec l'utilisateur correctement assigné
        Budget budget = Budget.builder()
                .utilisateur(utilisateur)  // 🔥 Assigne bien l'utilisateur !
                .montantAlloue(montantAlloue)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        // ✅ Sauvegarde du budget
        return budgetRepository.save(budget);
    }


    // ✅ Récupérer les budgets d'un utilisateur (Correction)
    @Override
    public List<Budget> getBudgetsParUtilisateur(Integer utilisateurId) {
        return budgetRepository.findByUtilisateurId(utilisateurId);  // ✅ Utilisation de `findByUtilisateurId()`
    }


    // ✅ Allouer un budget à une catégorie
    @Override
    @Transactional
    public BudgetCategorie allouerBudgetCategorie(Integer budgetId, Integer categorieId, BigDecimal montant) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("❌ Budget introuvable !"));

        Category category = categoryRepository.findById(categorieId)
                .orElseThrow(() -> new RuntimeException("❌ Catégorie introuvable !"));

        BigDecimal montantTotalAlloue = budgetCategorieRepository.findByBudgetId(budgetId)
                .stream()
                .map(BudgetCategorie::getMontantAlloue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (montantTotalAlloue.add(montant).compareTo(budget.getMontantAlloue()) > 0) {
            throw new RuntimeException("❌ Impossible d'allouer ce montant, le budget total serait dépassé !");
        }

        Optional<BudgetCategorie> existingCategoryBudget = budgetCategorieRepository.findByBudgetIdAndCategoryId(budgetId, categorieId);
        if (existingCategoryBudget.isPresent()) {
            throw new RuntimeException("⚠️ Une allocation existe déjà pour cette catégorie !");
        }

        BudgetCategorie budgetCategorie = BudgetCategorie.builder()
                .budget(budget)
                .category(category)  // ✅ Correction de `category()`
                .montantAlloue(montant)
                .montantDepense(BigDecimal.ZERO)
                .build();

        return budgetCategorieRepository.save(budgetCategorie);
    }

    // ✅ Calcul du budget restant
    @Override
    public BigDecimal getBudgetRestant(Integer budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("❌ Budget introuvable !"));

        BigDecimal montantTotalDepense = budgetCategorieRepository.findByBudgetId(budgetId)
                .stream()
                .map(BudgetCategorie::getMontantDepense)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return budget.getMontantAlloue().subtract(montantTotalDepense);
    }
}
