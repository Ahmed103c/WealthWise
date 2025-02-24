package com.Ahmed.repositories;

import com.Ahmed.Banking.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByCompteId(Integer compteId);
    // ✅ Utilise `In` pour rechercher plusieurs compteIds
    List<Transaction> findByCompteIdIn(List<Integer> compteIds);

    @Query("SELECT t FROM Transaction t WHERE t.compte.utilisateur.id = :userId")
    List<Transaction> findTransactionsByUserId(@Param("userId") Integer userId);

}
