package com.Ahmed.Banking.models;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Compte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nom;
    private String externalId;
    private String institution;
    private String iban;

    @Column(nullable = false)
    @Builder.Default
    private String currency = "EUR";

    @Column(precision = 19, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur", nullable = true)
    @JsonBackReference  // 🚀 Empêche la sérialisation infinie


    private Utilisateur utilisateur; // ✅ Propriétaire du compte

    @Column(nullable = false)
    @Builder.Default
    private boolean isConjoint = false; // ✅ On garde seulement celui-là !



    @OneToMany(mappedBy = "compte", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonBackReference  // 🚀 Empêche la sérialisation infinie


    private List<PartCompte> parts;

    /**
     * ✅ Vérifie si un compte est conjoint.
     */
    public boolean isCompteConjoint() {
        return isConjoint;
    }
    @Override
    public String toString() {
        return "Compte{" +
                "id=" + id +
                ", iban='" + iban + '\'' +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                // Supprimer utilisateurs pour éviter la boucle infinie
                '}';
    }

}
