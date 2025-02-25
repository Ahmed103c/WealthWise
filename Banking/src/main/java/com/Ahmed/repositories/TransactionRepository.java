package com.Ahmed.repositories;

import com.Ahmed.Banking.models.RecurrenceFrequency;
import com.Ahmed.Banking.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByCompteId(Integer compteId);

    @Query("SELECT t FROM Transaction t WHERE t.compte.utilisateur.id = :userId")
    List<Transaction> findTransactionsByUserId(@Param("userId") Integer userId);
    @Query("SELECT t FROM Transaction t WHERE t.recurrenceFrequency <> 'NONE' AND t.transactionDate <= :today")
    List<Transaction> findTransactionsRecurrentesActives(LocalDate today);
    /**
     * ✅ Trouver toutes les transactions récurrentes qui ne sont pas de type "NONE".
     */
    @Query("SELECT t FROM Transaction t WHERE t.recurrenceFrequency <> :frequency")
    List<Transaction> findAllByRecurrenceFrequencyNot(@Param("frequency") RecurrenceFrequency frequency);
}
