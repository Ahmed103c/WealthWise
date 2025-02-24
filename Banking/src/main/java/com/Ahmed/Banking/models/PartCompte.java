package com.Ahmed.Banking.models;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PartCompte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur; // ✅ Utilisateur propriétaire d'une part du compte

    @ManyToOne
    @JoinColumn(name = "id_compte", nullable = false)
    private Compte compte; // ✅ Référence au compte bancaire

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal pourcentage; // ✅ Pourcentage de propriété (ex: 50%, 25%)
    // ✅ Constructor to match the one used in CompteService
    public PartCompte(Utilisateur utilisateur, Compte compte, BigDecimal pourcentage) {
        this.utilisateur = utilisateur;
        this.compte = compte;
        this.pourcentage = pourcentage;
    }
}


