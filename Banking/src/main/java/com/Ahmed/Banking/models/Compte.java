package com.Ahmed.Banking.models;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
public class Compte {
    @Id
    @GeneratedValue
    private Integer id;

    
    
    private Integer solde;

    @ManyToOne
    @JoinColumn(name="id_utilisateur")
    private Utilisateur utilisateur;


    @OneToMany(mappedBy = "compte")
    private List<Transaction> transactions;

}
