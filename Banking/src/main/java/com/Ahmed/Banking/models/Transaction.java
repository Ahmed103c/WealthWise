package com.Ahmed.Banking.models;

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
    private Compte compte;

    private String description;

    @Enumerated(EnumType.STRING)
    private RecurrenceFrequency recurrenceFrequency;

    private LocalDate recurrenceEnd;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
