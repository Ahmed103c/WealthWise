package com.Ahmed.Banking.models;

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

    private String externalId;  // The ID from the bank API
    private String institution;
    private String iban;
    private String currency="EUR";
    private Double balance;

    @ManyToOne
    @JoinColumn(name="id_utilisateur")
    private Utilisateur utilisateur;

}
