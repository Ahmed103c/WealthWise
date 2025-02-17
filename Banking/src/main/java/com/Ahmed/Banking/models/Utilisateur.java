package com.Ahmed.Banking.models;


import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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


    @OneToMany(mappedBy = "utilisateur")
    private List<Compte> comptes;
    
}
