package com.Ahmed.Banking.services;

import com.Ahmed.Banking.dto.UtilisateurDto;
import com.Ahmed.Banking.models.Compte;
import com.Ahmed.Banking.models.Utilisateur;
import java.util.List;


public interface UtilisateurService extends AbstractService<UtilisateurDto>{
    public UtilisateurDto findByMail(String email);
    List<Compte> getComptesByUserId(Integer idUtilisateur);


}