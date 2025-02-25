package com.Ahmed.Banking.controllers;

import com.Ahmed.Banking.models.Transaction;
import com.Ahmed.Banking.services.Implementations.TransactionPlanifieeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions/planifiees")  // 🌍 Base de l'URL
@RequiredArgsConstructor
public class TransactionPlanifieeController {

    private final TransactionPlanifieeService transactionPlanifieeService;

    // ✅ 1️⃣ Endpoint pour récupérer toutes les transactions planifiées
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactionsPlanifiees() {
        List<Transaction> transactions = transactionPlanifieeService.getTransactionsPlanifiees();
        return ResponseEntity.ok(transactions);
    }

    // ✅ 2️⃣ Endpoint pour exécuter les transactions planifiées (manuellement)
    @PostMapping("/executer")
    public ResponseEntity<String> executerTransactions() {
        transactionPlanifieeService.executerTransactionsPlanifiees();
        return ResponseEntity.ok("✅ Transactions planifiées exécutées avec succès !");
    }
}
