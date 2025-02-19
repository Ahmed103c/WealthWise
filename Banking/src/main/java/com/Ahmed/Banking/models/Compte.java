package com.Ahmed.Banking.models;

import java.math.BigDecimal;
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

    private String externalId;
    private String institution;
    private String iban;
    private String currency = "EUR";

    @Column(precision = 19, scale = 2)  // ✅ Ajout pour éviter pertes de précision
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name="id_utilisateur")
    private Utilisateur utilisateur;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
