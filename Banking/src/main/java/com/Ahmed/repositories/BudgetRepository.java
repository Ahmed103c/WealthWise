package com.Ahmed.repositories;



import com.Ahmed.Banking.models.Budget;
import com.Ahmed.Banking.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    List<Budget> findByUtilisateurId(Integer utilisateurId);
    List<Budget> findByUtilisateur(Utilisateur utilisateur);
}

