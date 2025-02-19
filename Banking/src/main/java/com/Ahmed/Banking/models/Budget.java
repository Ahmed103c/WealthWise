package com.Ahmed.Banking.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

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
}
