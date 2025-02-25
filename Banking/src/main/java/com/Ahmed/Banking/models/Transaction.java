package com.Ahmed.Banking.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entité Transaction, stockée en BDD.
 * L'ID est de type Integer et auto-généré.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction") // Nom de table si besoin
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ou sequence
    private Integer id;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // "TRANSFER", "DEPOSIT", ...

    private LocalDate transactionDate;

    @ManyToOne
    @JoinColumn(name = "compte_id")
    @JsonIgnore  // ✅ Évite la boucle infinie en empêchant la sérialisation de Compte
    private Compte compte;



    private String description;

    @Enumerated(EnumType.STRING)
    private RecurrenceFrequency recurrenceFrequency;

    private LocalDate recurrenceEnd;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    public boolean isRecurrent() {
        return recurrenceFrequency != null && recurrenceFrequency != RecurrenceFrequency.NONE;
    }

    public LocalDate getNextExecutionDate() {
        if (!isRecurrent()) {
            return null;
        }
        switch (recurrenceFrequency) {
            case DAILY:
                return transactionDate.plusDays(1);
            case WEEKLY:
                return transactionDate.plusWeeks(1);
            case MONTHLY:
                return transactionDate.plusMonths(1);
            case YEARLY:
                return transactionDate.plusYears(1);
            default:
                return null;
        }
    }

}
