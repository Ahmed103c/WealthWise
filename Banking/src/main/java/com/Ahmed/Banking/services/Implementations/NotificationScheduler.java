package com.Ahmed.Banking.services.Implementations;


import com.Ahmed.Banking.models.*;
import com.Ahmed.Banking.services.BudgetService;
import com.Ahmed.Banking.services.NotificationService;
import com.Ahmed.repositories.*;
import com.Ahmed.Banking.services.Implementations.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationScheduler {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final CompteRepository compteRepository;
    private final PartCompteRepository partCompteRepository ;


    // ✅ **1️⃣ Notification automatique pour prélèvements à venir**
    //@Scheduled(cron = "0 1 0 * * ?") // Exécution quotidienne à 00:01
    public void notifierPrelevementsAVenir() {
        List<Transaction> transactions = transactionRepository.findAllByRecurrenceFrequencyNot(RecurrenceFrequency.NONE);
        LocalDate today = LocalDate.now();

        for (Transaction transaction : transactions) {
            if (transaction.getTransactionDate().equals(today.plusDays(1))) { // ✅ Prévenir 1 jour avant
                notificationService.creerNotification(
                        transaction.getCompte().getUtilisateur(),
                        NotificationType.PRELEVEMENT,
                        "📢 Un prélèvement de " + transaction.getAmount() + "€ est prévu demain."
                );
            }
        }
    }

    // ✅ **2️⃣ Notification automatique pour transactions récurrentes exécutées**
    // @Scheduled(cron = "0 2 0 * * ?") // Exécution quotidienne à 00:02
    public void notifierTransactionsRecursives() {
        List<Transaction> transactions = transactionRepository.findTransactionsRecurrentesActives(LocalDate.now());

        for (Transaction transaction : transactions) {
            notificationService.creerNotification(
                    transaction.getCompte().getUtilisateur(),
                    NotificationType.TRANSACTION_RECURRENTE,
                    "♻️ Une transaction récurrente de " + transaction.getAmount() + "€ a été effectuée."
            );
        }
    }

    //@Scheduled(cron = "0 3 0 * * ?") // Exécution quotidienne à 00:03
    public void verifierDepassementBudget() {
        List<Budget> budgets = budgetRepository.findAll();
        for (Budget budget : budgets) {
            // 🔥 Vérifie si la liste `budgetCategories` est nulle pour éviter NullPointerException
            List<BudgetCategorie> categories = budget.getBudgetCategories();
            BigDecimal montantDepense = categories != null
                    ? categories.stream()
                    .map(BudgetCategorie::getMontantDepense)
                    .reduce(BigDecimal.ZERO, (a, b) -> a.add(b)) // ✅ Utilisation correcte de `add()`
                    : BigDecimal.ZERO;

            BigDecimal budgetRestant = budget.getMontantAlloue().subtract(montantDepense);

            if (budgetRestant.compareTo(BigDecimal.ZERO) < 0) {
                notificationService.creerNotification(
                        budget.getUtilisateur(),
                        NotificationType.DEPASSEMENT_BUDGET,
                        "🚨 Vous avez dépassé votre budget de " + budgetRestant.abs() + "€ !"
                );
            }
        }
    }
    @Scheduled(cron = "0 0/5 * * * ?") // Exécution toutes les 5 minutes (à ajuster selon vos besoins)
    public void notifierDecouvertEtDepassementPart() {
        // Récupérer tous les comptes
        List<Compte> comptes = compteRepository.findAll();

        for (Compte compte : comptes) {
            if (!compte.isConjoint()) {
                // Pour un compte individuel : si le solde est négatif, notifier l'utilisateur
                if (compte.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                    notificationService.creerNotification(
                            compte.getUtilisateur(),
                            NotificationType.DEPASSEMENT_BUDGET,  // ou un type "DECOUVERT" si vous en avez défini un
                            "⚠️ Votre compte '" + compte.getNom() + "' est à découvert."
                    );
                }
            } else {
                // Pour un compte conjoint : si le solde global est négatif, cela signifie que la part allouée à chaque utilisateur est dépassée
                if (compte.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                    // Récupérer les parts associées à ce compte
                    List<PartCompte> parts = partCompteRepository.findByCompteId(compte.getId());
                    for (PartCompte part : parts) {
                        notificationService.creerNotification(
                                part.getUtilisateur(),
                                NotificationType.DEPASSEMENT_BUDGET, // ou "DEPASSEMENT_PART" si vous préférez distinguer
                                "⚠️ Votre part dans le compte conjoint '" + compte.getNom() + "' est dépassée."
                        );
                    }
                }
            }
        }
    }


}
