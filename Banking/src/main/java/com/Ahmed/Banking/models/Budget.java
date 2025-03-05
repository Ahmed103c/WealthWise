package com.Ahmed.Banking.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne  // ✅ Correction : Ajout de la relation avec Utilisateur
    @JoinColumn(name = "utilisateur_id", nullable = false)  // ✅ Assure un bon mapping SQL
    private Utilisateur utilisateur;  // ✅ Modification de `userId` à `utilisateur`

    @Column(nullable = false)
    private BigDecimal montantAlloue;

    private LocalDate startDate;
    private LocalDate endDate;
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<BudgetCategorie> budgetCategories;

}
