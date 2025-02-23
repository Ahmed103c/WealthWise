package com.Ahmed.Banking.controllers;

import com.Ahmed.Banking.models.Compte;
import com.Ahmed.Banking.services.Implementations.CompteService;
import com.Ahmed.Banking.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

import java.util.List;

@RestController
@RequestMapping("/api/comptes") // ✅ Base URL for account management
public class CompteController {

    private final CompteService compteService;
    private final TransactionService transactionService;

    public CompteController(CompteService compteService, TransactionService transactionService) {
        this.compteService = compteService;
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<?> createCompte(@RequestBody Compte compte) {
        try {
            Compte savedCompte = compteService.saveCompte(compte);
            return ResponseEntity.ok(savedCompte);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "❌ Impossible de créer le compte !"));
        }
    }


    // ✅ 2️⃣ Retrieve all accounts for a specific user
    @GetMapping("/utilisateur/{userId}")
    public ResponseEntity<?> getComptesByUtilisateur(@PathVariable Integer userId) {
        try {
            List<Compte> comptes = compteService.getComptesByUtilisateurId(userId);
            return ResponseEntity.ok(comptes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Error fetching accounts: " + e.getMessage());
        }
    }

    @PostMapping("/authenticate/{userId}")
    public ResponseEntity<?> authenticateUserBanking(@PathVariable Integer userId) {
        try {
            Map<String, String> authData = compteService.automateCompteFetching(userId);
            return ResponseEntity.ok(authData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "❌ Error generating authentication link: " + e.getMessage()));
        }
    }



    @PostMapping("/fetch-accounts/{requisitionId}/{userId}")
    public ResponseEntity<Map<String, Object>> fetchUserAccounts(@PathVariable String requisitionId, @PathVariable Integer userId) {
        try {
            List<String> accounts = compteService.fetchAndSaveUserAccounts(requisitionId, userId);

            // ✅ Récupération automatique des transactions après import des comptes
            String accessToken = compteService.getAccessToken();
            for (String accountId : accounts) {
                try {
                    transactionService.fetchAndSaveTransactions(accessToken, accountId);
                } catch (Exception e) {
                    System.err.println("⚠️ Erreur lors de l'importation des transactions du compte " + accountId + " : " + e.getMessage());
                }
            }

            // ✅ Réponse JSON
            Map<String, Object> response = new HashMap<>();
            response.put("message", "✅ Accounts linked successfully");
            response.put("accounts", accounts);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "❌ Error fetching user accounts: " + e.getMessage()));
        }
    }



    // ✅ 5️⃣ Fetch transactions from GoCardless and save in the database
    @PostMapping("/fetch-transactions/{accountId}")
    public ResponseEntity<?> fetchTransactions(@PathVariable String accountId) {
        try {
            String accessToken = compteService.getAccessToken();
            transactionService.fetchAndSaveTransactions(accessToken, accountId);
            return ResponseEntity.ok("✅ Transactions imported successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Error fetching transactions: " + e.getMessage());
        }
    }
}
