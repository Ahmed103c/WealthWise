package com.Ahmed.Banking.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Ahmed.Banking.dto.UtilisateurDto;
import com.Ahmed.Banking.services.UtilisateurService;
import com.Ahmed.Banking.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import java.util.HashMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.Ahmed.Banking.models.Utilisateur;
import com.Ahmed.Banking.models.Compte;
import com.Ahmed.Banking.security.JwtTokenProvider;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.List;



import java.util.List;
@Tag(name = "Utilisateur", description = "Endpoints pour la gestion des utilisateurs")
@RestController
@RequestMapping("/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;  // Injection de JwtTokenProvider

    @Autowired
    private UtilisateurService service;  // Injection de ton service utilisateur

    @PostMapping("/")
    public ResponseEntity<Integer> save(@RequestBody UtilisateurDto utilisateurDto)
    {
        return ResponseEntity.ok(service.save(utilisateurDto)); //si y a exception elle sera levée directement
    }

    @GetMapping("/")
    public ResponseEntity<List<UtilisateurDto>> findAll()
    {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{utilisateur-id}")
    public ResponseEntity<UtilisateurDto> findById(@PathVariable("utilisateur-id") Integer id )
    {
        return ResponseEntity.ok(service.findById(id));
    }

    @DeleteMapping("/{utilisateur-id}")
    public ResponseEntity<Void> delete(@PathVariable("utilisateur-id") Integer id )
    {
        service.delete(id);
        return ResponseEntity.accepted().build();
    }


    @Operation(summary = "Authentification utilisateur", description = "Permet de se connecter et récupérer un JWT.")
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Map<String, String>> login(
            @Parameter(description = "Email de l'utilisateur", required = true) @RequestParam String email,
            @Parameter(description = "Mot de passe de l'utilisateur", required = true) @RequestParam String password) {

        try {
            Utilisateur utilisateur = service.findByMail(email);

            if (utilisateur != null && isPasswordValid(password, utilisateur.getMotDePasse())) {
                String token = jwtTokenProvider.generateToken(email);

                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid password"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
    }



    // Méthode pour vérifier si le mot de passe est valide (à adapter selon ta logique de hachage)
    private boolean isPasswordValid(String inputPassword, String storedPassword) {
        // Utiliser par exemple BCrypt pour comparer le mot de passe
        return inputPassword.equals(storedPassword);  // Remplace par la méthode de validation adéquate (par exemple, BCrypt)
    }
    @GetMapping("/profil")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String authorizationHeader) {
        System.out.println("🔹 Header Authorization reçu : " + authorizationHeader);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Token manquant ou invalide");
        }

        String token = authorizationHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getUsernameFromToken(token);

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Impossible d'extraire l'email du token");
        }

        // 🔹 Récupération de l'utilisateur en DTO (pour respecter `findByMail`)
        Utilisateur utilisateur = service.findByMail(email);
        System.out.println("🔹 Utilisateur trouvé : " + utilisateur);
        System.out.println("🔹 ID de l'utilisateur : " + utilisateur.getId());

        // 🔹 Récupération des comptes liés
        List<Compte> comptes = service.getComptesByUserId(utilisateur.getId());

        // 🔹 Création de l'objet JSON de réponse
        Map<String, Object> response = new HashMap<>();
        response.put("nom", utilisateur.getNom());
        response.put("prenom", utilisateur.getPrenom());
        response.put("email", utilisateur.getEmail());
        response.put("comptes", comptes);

        return ResponseEntity.ok(response);
    }



}
