package com.Ahmed.Banking.controllers;

import com.Ahmed.Banking.models.Budget;
import com.Ahmed.Banking.models.BudgetCategorie;
import com.Ahmed.Banking.models.Utilisateur;
import com.Ahmed.Banking.services.BudgetService;
import com.Ahmed.repositories.UtilisateurRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    private final BudgetService budgetService;
    private final UtilisateurRepository utilisateurRepository;

    public BudgetController(BudgetService budgetService, UtilisateurRepository utilisateurRepository) {
        this.budgetService = budgetService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> creerBudget(
            @RequestParam("userId") Integer userId,
            @RequestParam("montantAlloue") BigDecimal montantAlloue,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (userId == null) {
            return ResponseEntity.badRequest().body("❌ L'utilisateur est requis pour créer un budget !");
        }

        Budget newBudget;
        try {
            newBudget = budgetService.creerBudget(userId, montantAlloue, "mensuel");
            newBudget.setStartDate(startDate);
            newBudget.setEndDate(endDate);
            newBudget = budgetService.saveBudget(newBudget);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ Erreur : " + e.getMessage());
        }
        return ResponseEntity.ok(newBudget);
    }

    @GetMapping("/{utilisateurId}")
    public ResponseEntity<?> getBudgets(@PathVariable Integer utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId).orElse(null);
        if (utilisateur == null) {
            return ResponseEntity.badRequest().body("❌ Utilisateur introuvable !");
        }
        List<Budget> budgets = budgetService.getBudgetsParUtilisateur(utilisateurId);
        return ResponseEntity.ok(budgets);
    }

    @PostMapping("/allouer")
    public ResponseEntity<?> allouerBudget(
            @RequestParam Integer budgetId,
            @RequestParam Integer categorieId,
            @RequestParam BigDecimal montant) {
        BudgetCategorie budgetCategorie;
        try {
            budgetCategorie = budgetService.allouerBudgetCategorie(budgetId, categorieId, montant);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ Erreur : " + e.getMessage());
        }
        return ResponseEntity.ok(budgetCategorie);
    }
}
