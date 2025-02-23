package com.Ahmed.Banking.services.Implementations;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.Ahmed.Banking.dto.UtilisateurDto;
import com.Ahmed.Banking.models.Utilisateur;
import com.Ahmed.Banking.models.Compte;
import com.Ahmed.Banking.services.UtilisateurService;
import com.Ahmed.Banking.validators.ObjectsValidator;
import com.Ahmed.repositories.UtilisateurRepository;
import com.Ahmed.repositories.CompteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UtilisateurService {
    private final UtilisateurRepository repository;
    private final ObjectsValidator<UtilisateurDto> validator;
    private final CompteRepository compteRepository;

    @Override
    public Integer save(UtilisateurDto utilisateurDto) {
        if (repository.findByEmail(utilisateurDto.getEmail()).isPresent()) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà !");
        }
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(utilisateurDto.getNom());
        utilisateur.setPrenom(utilisateurDto.getPrenom());
        utilisateur.setEmail(utilisateurDto.getEmail());
        utilisateur.setMotDePasse(utilisateurDto.getMotDePasse());
        return repository.save(utilisateur).getId();
    }

    @Override
    public List<UtilisateurDto> findAll() {
        return repository.findAll()
                .stream()
                .map(UtilisateurDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public UtilisateurDto findById(Integer id) {
        return repository.findById(id)
                .map(UtilisateurDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("No user was found with the provided id" + id));
    }



    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public UtilisateurDto findByMail(String email) {
        return repository.findByEmail(email)
                .map(UtilisateurDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("No user was found with the provided email: " + email));
    }

    public boolean emailExists(String email) {
        return repository.findByEmail(email).isPresent();
    }
    @Override
    public List<Compte> getComptesByUserId(Integer userId) {
        return compteRepository.findByUtilisateurId(userId);
    }

}
