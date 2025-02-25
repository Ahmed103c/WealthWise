package com.Ahmed.Banking.models;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String motDePasse;  // ✅ Sécurisé avec @JsonIgnore

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // ✅ Ignore cette liste lors de la sérialisation pour éviter les boucles
    private List<Compte> comptes;


    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference  // ✅ Correction pour la sérialisation
    private List<PartCompte> partComptes;  // ✅ Liste des parts de comptes conjoints

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;  // ✅ Précision + valeur par défaut

    /**
     * ✅ Calcule la balance totale de l'utilisateur en additionnant :
     * - Le solde de ses comptes individuels
     * - Sa part des comptes conjoints
     */
    public BigDecimal calculerBalance() {
        BigDecimal total = BigDecimal.ZERO;

        // Additionner la balance des comptes individuels uniquement (exclure les comptes joints)
        if (comptes != null) {
            for (Compte compte : comptes) {
                if (!compte.isConjoint()) {
                    total = total.add(Optional.ofNullable(compte.getBalance()).orElse(BigDecimal.ZERO));
                }
            }
        }

        // Ajouter la part des comptes joints (chaque part est calculée selon le pourcentage)
        if (partComptes != null) {
            for (PartCompte part : partComptes) {
                if (part.getCompte() != null && part.getCompte().getBalance() != null) {
                    BigDecimal partBalance = part.getCompte().getBalance()
                            .multiply(part.getPourcentage())
                            .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                    total = total.add(partBalance);
                }
            }
        }

        return total;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }


    // Dans Utilisateur.java
    public void mettreAJourBalance() {
        this.balance = calculerBalance();
    }


}


