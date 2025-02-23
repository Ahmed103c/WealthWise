package com.Ahmed.repositories;

import com.Ahmed.Banking.models.PartCompte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartCompteRepository extends JpaRepository<PartCompte, Integer> {

    List<PartCompte> findByCompteId(Integer compteId);

    Optional<PartCompte> findByCompteIdAndUtilisateurId(Integer compteId, Integer utilisateurId);
}
