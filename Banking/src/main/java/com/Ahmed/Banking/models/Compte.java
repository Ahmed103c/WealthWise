package com.Ahmed.Banking.models;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonBackReference // ✅ Correction de la boucle infinie
    private Utilisateur utilisateur;



    @Column(nullable = false)
    @Builder.Default
    private boolean isConjoint = false; // ✅ On garde seulement celui-là !



    @OneToMany(mappedBy = "compte", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore  // ✅ Empêche Jackson d’essayer de charger les relations lors de la désérialisation
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
                ", nom='" + nom + '\'' +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                ", isConjoint=" + isConjoint +
                '}';
    }
    // ✅ Constructor to match the one used in CompteService
    public Compte(String nom, String externalId, String institution, String iban,
                  String currency, BigDecimal balance, boolean isConjoint) {
        this.nom = nom;
        this.externalId = externalId;
        this.institution = institution;
        this.iban = iban;
        this.currency = currency;
        this.balance = balance;
        this.isConjoint = isConjoint;
    }




}
