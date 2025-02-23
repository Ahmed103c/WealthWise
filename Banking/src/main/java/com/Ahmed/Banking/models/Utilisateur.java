package com.Ahmed.Banking.models;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
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

    @JsonIgnore
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference  // 🚀 Empêche la sérialisation infinie

    private List<Compte> comptes;  // ✅ Liste des comptes individuels

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference  // 🚀 Empêche la sérialisation infinie

    private List<PartCompte> partComptes;  // ✅ Liste des parts de comptes conjoints

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;  // ✅ Précision + valeur par défaut

    /**
     * Calcule la balance totale de l'utilisateur en additionnant :
     * - Le solde de ses comptes individuels
     * - Sa part des comptes conjoints
     */
    public BigDecimal calculerBalance() {
        BigDecimal total = BigDecimal.ZERO;

        // Additionner les balances des comptes individuels
        if (comptes != null) {
            for (Compte compte : comptes) {
                total = total.add(Optional.ofNullable(compte.getBalance()).orElse(BigDecimal.ZERO));
            }
        }

        // Ajouter les parts des comptes conjoints
        if (partComptes != null) {
            for (PartCompte part : partComptes) {
                BigDecimal partBalance = Optional.ofNullable(part.getCompte().getBalance()).orElse(BigDecimal.ZERO)
                        .multiply(part.getPourcentage()).divide(BigDecimal.valueOf(100));
                total = total.add(partBalance);
            }
        }

        return total;
    }
    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                // Supprimer comptes pour éviter la boucle infinie
                '}';
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

}
