package com.Ahmed.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Ahmed.Banking.models.Utilisateur;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur,Integer> 
{
    Optional<Utilisateur> findByEmail(String email);
    /* 
    // SELECT * FROM UTILISATEURS WHERE nom = 'Rzeigui';
    List<Utilisateur> findAllByNom(String nom);

    // ou bien query : 
    @Query("from Utilisateur WHERE nom = :fn")// pas d'espace entre :et fn !!
    List<Utilisateur> searchByFirstname(@Param("fn") String firstname);


    // SELECT * FROM UTILISATEUR U inner join compte c on U.id=c.id_utilisteur and c.iban="DE12345678"
    List<Utilisateur> findAllByCompteId(Integer Id);


    @Query("fROM UTILISATEUR U inner join compte c on U.id=c.id_utilisteur where c.id= :iban")
    List<Utilisateur> searchByCompte(Integer iban);

    @Query(value="SELECT * fROM UTILISATEUR U inner join compte c on U.id=c.id_utilisteur where c.id= :iban",nativeQuery = true)
    List<Utilisateur> searchByCompteNative(Integer iban);
    */



    // Recherche tous les utilisateurs avec un nom donné
    List<Utilisateur> findAllByNom(String nom);

    // Recherche par prénom avec une requête JPQL
    @Query("FROM Utilisateur u WHERE u.nom = :fn")
    List<Utilisateur> searchByFirstname(@Param("fn") String firstname);

    // // Recherche tous les utilisateurs ayant un compte avec un identifiant donné
    // List<Utilisateur> findAllByCompteId(Integer id);

    // Recherche par IBAN en JPQL (à condition que la relation entre Utilisateur et Compte soit configurée)
    // @Query("SELECT u FROM Utilisateur u JOIN u.compte c WHERE c.id = :id")
    // List<Utilisateur> searchByCompte(@Param("iban") String iban);

    // Recherche par IBAN avec une requête SQL native
    @Query(value = "SELECT * FROM Utilisateur u INNER JOIN Compte c ON u.id = c.utilisateur_id WHERE c.iban = :iban", nativeQuery = true)
    List<Utilisateur> searchByCompteNative(@Param("iban") String iban);



}
