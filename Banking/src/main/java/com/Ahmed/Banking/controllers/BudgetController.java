package com.Ahmed.Banking.controllers;

import com.Ahmed.Banking.models.Budget;
import com.Ahmed.Banking.models.BudgetCategorie;
import com.Ahmed.Banking.models.Utilisateur;
import com.Ahmed.Banking.services.BudgetService;
import com.Ahmed.repositories.UtilisateurRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    // ✅ Création d'un budget avec validation
    @PostMapping("/create")
    public ResponseEntity<?> creerBudget(@RequestBody Budget budget) {
        if (budget.getUtilisateur() == null || budget.getUtilisateur().getId() == null) {
            return ResponseEntity.badRequest().body("❌ L'utilisateur est requis pour créer un budget !");
        }

        Budget newBudget;
        try {
            newBudget = budgetService.creerBudget(
                    budget.getUtilisateur().getId(),  // ✅ Correction
                    budget.getMontantAlloue(),
                    "mensuel"  // 📌 Remplace par `budget.getPeriode()` si le champ existe dans `Budget`
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ Erreur : " + e.getMessage());
        }

        return ResponseEntity.ok(newBudget);
    }

    // ✅ Récupérer les budgets par utilisateur
    @GetMapping("/{utilisateurId}")
    public ResponseEntity<?> getBudgets(@PathVariable Integer utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElse(null);

        if (utilisateur == null) {
            return ResponseEntity.badRequest().body("❌ Utilisateur introuvable !");
        }

        List<Budget> budgets = budgetService.getBudgetsParUtilisateur(utilisateurId);
        if (budgets.isEmpty()) {
            return ResponseEntity.ok("⚠️ Aucun budget trouvé pour cet utilisateur.");
        }

        return ResponseEntity.ok(budgets);
    }

    // ✅ Allouer un budget à une catégorie
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
