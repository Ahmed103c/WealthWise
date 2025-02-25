package com.Ahmed.Banking.services.Implementations;

import com.Ahmed.Banking.models.*;
import com.Ahmed.repositories.TransactionRepository;
import com.Ahmed.repositories.CompteRepository;
import com.Ahmed.Banking.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionPlanifieeService {

    private final TransactionRepository transactionRepository;
    private final CompteRepository compteRepository;
    private final NotificationService notificationService;

    // ✅ Vérifie chaque jour à 00:01 si des transactions planifiées doivent être exécutées
    @Scheduled(cron = "0 1 0 * * ?")
    public void executerTransactionsPlanifiees() {
        // ✅ Utilisation de la méthode corrigée `findAllByRecurrenceFrequencyNot`
        List<Transaction> transactions = transactionRepository.findAllByRecurrenceFrequencyNot(RecurrenceFrequency.NONE);

        for (Transaction transaction : transactions) {
            if (transaction.getTransactionDate().isBefore(LocalDate.now()) &&
                    (transaction.getRecurrenceEnd() == null || transaction.getTransactionDate().isBefore(transaction.getRecurrenceEnd()))) {

                // ✅ Exécuter la transaction et mettre à jour le solde du compte
                Compte compte = transaction.getCompte();
                if (compte != null) {
                    compte.setBalance(compte.getBalance().subtract(transaction.getAmount()));
                    compteRepository.save(compte);

                    // ✅ Dupliquer la transaction pour la prochaine exécution
                    Transaction nouvelleTransaction = Transaction.builder()
                            .amount(transaction.getAmount())
                            .type(transaction.getType())
                            .transactionDate(transaction.getNextExecutionDate())  // Assurez-vous que cette méthode existe !
                            .compte(compte)
                            .description(transaction.getDescription())
                            .recurrenceFrequency(transaction.getRecurrenceFrequency())
                            .recurrenceEnd(transaction.getRecurrenceEnd())
                            .category(transaction.getCategory())
                            .build();

                    transactionRepository.save(nouvelleTransaction);

                    // ✅ Envoyer une notification
                    notificationService.creerNotification(
                            compte.getUtilisateur(),
                            NotificationType.TRANSACTION_RECURRENTE,
                            "♻️ Une transaction récurrente de " + transaction.getAmount() + "€ a été effectuée."
                    );
                }
            }
        }
    }
    public List<Transaction> getTransactionsPlanifiees() {
        return transactionRepository.findAllByRecurrenceFrequencyNot(RecurrenceFrequency.NONE);
    }

}
