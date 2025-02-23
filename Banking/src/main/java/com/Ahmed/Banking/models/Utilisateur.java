package com.Ahmed.Banking.models;

import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Utilisateur {
    @Id
    @GeneratedValue
    private Integer id;

    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;

    @JsonIgnore
    @OneToMany(mappedBy = "utilisateur")
    private List<Compte> comptes;

    @Column(precision = 19, scale = 2)  // ✅ Précision pour éviter erreurs d'arrondi
    private BigDecimal balance = BigDecimal.ZERO;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

}
