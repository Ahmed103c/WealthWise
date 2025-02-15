package com.Ahmed.Banking.services.Implementations;

import java.util.List;
import java.util.stream.Collectors;

import com.Ahmed.Banking.dto.TransactionDto;
import org.springframework.stereotype.Service;

import com.Ahmed.Banking.dto.UtilisateurDto;
import com.Ahmed.Banking.models.Utilisateur;
import com.Ahmed.Banking.services.UtilisateurService;
import com.Ahmed.Banking.validators.ObjectsValidator;
import com.Ahmed.repositories.UtilisateurRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UtilisateurService  {
    //Injéction Dépendence :
    // methode 01 :
    // @Autowired
    // private UtilisateurRepository repository;

    //Méthode 02 :
    // private UtilisateurRepository repository;

    // public UserServiceImpl(UtilisateurRepository repository)
    // {
    //     this.repository=repository;
    // }

    //Méthode 03
    //ajouter final et @RequiredArgsConstructor
    private final UtilisateurRepository repository;
    private final ObjectsValidator<UtilisateurDto> validator;


    @Override
    public Integer save(UtilisateurDto dto) {
        //le gestionaire globale va gérer l'exception si elle est levé
        validator.validate(dto);

        Utilisateur utilisateur = UtilisateurDto.toEntity(dto);
        Utilisateur savedUtilisateur = repository.save(utilisateur); // cette méthode existe déja fil jpa repository


        return savedUtilisateur.getId();
    }

    @Override
    public List<UtilisateurDto> findAll() {
        return repository.findAll()
               .stream() //premettre de renvoyer liste Utilisateur : Stream<Utilisateur>
               .map(UtilisateurDto::fromEntity) // : Stream<UtilisateurDto>
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

}
