package com.Ahmed.Banking.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "budget_categorie")
public class BudgetCategorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "budget_id", nullable = false)
    @JsonBackReference
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "categorie_id", nullable = false)
    private Category category;


    private BigDecimal montantAlloue;

    private BigDecimal montantDepense;
}
