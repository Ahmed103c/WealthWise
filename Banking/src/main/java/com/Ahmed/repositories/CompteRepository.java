package com.Ahmed.repositories;


import com.Ahmed.Banking.models.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository



public interface CompteRepository extends JpaRepository<Compte, Integer> {

    // ✅ FIX: Ensure only ONE account is fetched for externalId
    Optional<Compte> findByExternalId(String externalId);

    // ✅ Retrieve all accounts for a specific user
    List<Compte> findByUtilisateurId(Integer userId);
}
