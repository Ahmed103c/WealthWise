package com.Ahmed.Banking.controllers;

import com.Ahmed.Banking.services.Implementations.NotificationScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-notifications")
@RequiredArgsConstructor
public class NotificationTestController {

    private final NotificationScheduler notificationScheduler;

    // 🔥 Tester les prélèvements à venir
    /*@PostMapping("/prelevements")
    public String testPrelevements() {
        notificationScheduler.notifierPrelevementsAVenir();
        return "✅ Test des notifications de prélèvements exécuté !";
    }

    // 🔥 Tester les transactions récurrentes
    @PostMapping("/transactions-recursives")
    public String testTransactionsRecursives() {
        notificationScheduler.notifierTransactionsRecursives();
        return "✅ Test des transactions récurrentes exécuté !";
    }

    // 🔥 Tester le dépassement de budget
    @PostMapping("/depassement-budget")
    public String testDepassementBudget() {
        notificationScheduler.verifierDepassementBudget();
        return "✅ Test du dépassement de budget exécuté !";
    }


    // 🔥 Tester le découvert / dépassement de part
    @PostMapping("/decouvert-depassement")
    public String testDecouvertDepassement() {
        notificationScheduler.notifierDecouvertEtDepassementPart();
        return "✅ Test des notifications de découvert/dépassement de part exécuté !";
    }
*/
}
