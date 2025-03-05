package com.Ahmed.Banking.services.Implementations;

import com.Ahmed.Banking.models.*;
import com.Ahmed.Banking.services.BudgetService;
import com.Ahmed.Banking.services.NotificationService;
import com.Ahmed.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    // ✅ Injection des dépendances avec `@RequiredArgsConstructor`
    private final BudgetRepository budgetRepository;
    private final BudgetCategorieRepository budgetCategorieRepository;
    private final CategoryRepository categoryRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;
    private final TransactionRepository transactionRepository;
    private final CompteRepository compteRepository;

    /**
     * ✅ **Créer un budget pour un utilisateur**
     * - Vérifie l'existence de l'utilisateur.
     * - Détermine les dates de début et de fin en fonction de la période.
     * - Vérifie si un budget existe déjà pour cette période.
     * - Enregistre et retourne le budget créé.
     */
    @Override
    @Transactional
    public Budget creerBudget(Integer utilisateurId, BigDecimal montantAlloue, String periode) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("❌ Utilisateur introuvable !"));

        // 🔄 Définition des périodes du budget
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

        // ✅ Vérifier si un budget existe déjà pour cette période
        Optional<Budget> existingBudget = budgetRepository.findByUtilisateur(utilisateur)
                .stream()
                .filter(b -> b.getStartDate().equals(startDate) && b.getEndDate().equals(endDate))
                .findFirst();

        if (existingBudget.isPresent()) {
            throw new RuntimeException("⚠️ Un budget pour cette période existe déjà !");
        }

        // ✅ Création et sauvegarde du budget
        Budget budget = Budget.builder()
                .utilisateur(utilisateur)
                .montantAlloue(montantAlloue)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return budgetRepository.save(budget);
    }

    /**
     * ✅ **Récupérer tous les budgets d'un utilisateur**
     */
    @Override
    public List<Budget> getBudgetsParUtilisateur(Integer utilisateurId) {
        return budgetRepository.findByUtilisateurId(utilisateurId);
    }

    /**
     * ✅ **Allouer un montant d'un budget à une catégorie**
     * - Vérifie si le budget et la catégorie existent.
     * - Vérifie que le montant ne dépasse pas le budget alloué.
     * - Vérifie si une allocation existe déjà pour cette catégorie.
     * - Enregistre l'allocation de budget à la catégorie.
     */
    @Override
    @Transactional
    public BudgetCategorie allouerBudgetCategorie(Integer budgetId, Integer categorieId, BigDecimal montant) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("❌ Budget introuvable !"));

        Category category = categoryRepository.findById(categorieId)
                .orElseThrow(() -> new RuntimeException("❌ Catégorie introuvable !"));

        // ✅ Vérifier que l'on ne dépasse pas le budget total
        BigDecimal montantTotalAlloue = budgetCategorieRepository.findByBudgetId(budgetId)
                .stream()
                .map(BudgetCategorie::getMontantAlloue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (montantTotalAlloue.add(montant).compareTo(budget.getMontantAlloue()) > 0) {
            throw new RuntimeException("❌ Impossible d'allouer ce montant, le budget total serait dépassé !");
        }

        // ✅ Vérifier si une allocation existe déjà pour cette catégorie
        Optional<BudgetCategorie> existingCategoryBudget = budgetCategorieRepository.findByBudgetIdAndCategoryId(budgetId, categorieId);
        if (existingCategoryBudget.isPresent()) {
            throw new RuntimeException("⚠️ Une allocation existe déjà pour cette catégorie !");
        }

        // ✅ Création et sauvegarde de l'allocation
        BudgetCategorie budgetCategorie = BudgetCategorie.builder()
                .budget(budget)
                .category(category)
                .montantAlloue(montant)
                .montantDepense(BigDecimal.ZERO)
                .build();

        return budgetCategorieRepository.save(budgetCategorie);
    }

    /**
     * ✅ **Calculer le budget restant**
     */
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

    /**
     * ✅ **Exécuter les transactions planifiées**
     * - Exécuté automatiquement chaque jour à 00:01.
     * - Applique les transactions récurrentes.
     * - Met à jour le solde des comptes concernés.
     * - Envoie des notifications aux utilisateurs.
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void executerTransactionsPlanifiees() {
        List<Transaction> transactions = transactionRepository.findTransactionsRecurrentesActives(LocalDate.now());

        for (Transaction transaction : transactions) {
            if (transaction.getTransactionDate().isBefore(LocalDate.now()) &&
                    (transaction.getRecurrenceEnd() == null || transaction.getTransactionDate().isBefore(transaction.getRecurrenceEnd()))) {

                Compte compte = transaction.getCompte();
                compte.setBalance(compte.getBalance().subtract(transaction.getAmount()));

                // ✅ Sauvegarde du compte mis à jour
                compteRepository.save(compte);

                // ✅ Création et sauvegarde de la nouvelle transaction
                Transaction nouvelleTransaction = Transaction.builder()
                        .amount(transaction.getAmount())
                        .type(transaction.getType())
                        .transactionDate(transaction.getTransactionDate().plusDays(30)) // Simulation d'une transaction mensuelle
                        .compte(compte)
                        .description(transaction.getDescription())
                        .recurrenceFrequency(transaction.getRecurrenceFrequency())
                        .recurrenceEnd(transaction.getRecurrenceEnd())
                        .category(transaction.getCategory())
                        .build();

                transactionRepository.save(nouvelleTransaction);

                // ✅ Envoi de la notification
                notificationService.creerNotification(
                        compte.getUtilisateur(),
                        NotificationType.TRANSACTION_RECURRENTE,
                        "♻️ Une transaction récurrente de " + transaction.getAmount() + "€ a été effectuée."
                );
            }
        }
    }
    @Override
    public Budget saveBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

}
