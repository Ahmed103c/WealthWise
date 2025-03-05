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

import java.math.BigDecimal;
import java.util.Objects;


import java.util.List;

@Tag(name = "Utilisateur", description = "Endpoints pour la gestion des utilisateurs")
@RestController
@RequestMapping("/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;  // Injection du JwtTokenProvider

    @Autowired
    private UtilisateurService service;  // Injection du service utilisateur

    @PostMapping("/")
    public ResponseEntity<Integer> save(@RequestBody UtilisateurDto utilisateurDto) {
        return ResponseEntity.ok(service.save(utilisateurDto));
    }

    @GetMapping("/")
    public ResponseEntity<List<UtilisateurDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{utilisateur-id}")
    public ResponseEntity<UtilisateurDto> findById(@PathVariable("utilisateur-id") Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @DeleteMapping("/{utilisateur-id}")
    public ResponseEntity<Void> delete(@PathVariable("utilisateur-id") Integer id) {
        service.delete(id);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Authentification utilisateur", description = "Permet de se connecter et récupérer un JWT.")
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Map<String, String>> login(
            @Parameter(description = "Email de l'utilisateur", required = true) @RequestParam String email,
            @Parameter(description = "Mot de passe de l'utilisateur", required = true) @RequestParam String password) {

        try {
            UtilisateurDto utilisateur = service.findByMail(email);
            if (utilisateur != null && isPasswordValid(password, utilisateur.getMotDePasse())) {
                // Génération du token en incluant l'ID utilisateur
                String token = jwtTokenProvider.generateToken(email, utilisateur.getId());
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

    // Vérification simple du mot de passe (à remplacer par une logique de hachage sécurisée en production)
    private boolean isPasswordValid(String inputPassword, String storedPassword) {
        return inputPassword.equals(storedPassword);
    }
    @GetMapping("/profil/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Integer userId) {
        UtilisateurDto utilisateur = service.findById(userId);
        if (utilisateur == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Utilisateur introuvable !");
        }

        List<Compte> comptes = service.getComptesByUserId(userId);
        BigDecimal balance = comptes.stream()
                .map(Compte::getBalance)
                .filter(Objects::nonNull) // Éviter les nulls
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> response = new HashMap<>();
        response.put("nom", utilisateur.getNom());
        response.put("prenom", utilisateur.getPrenom());
        response.put("email", utilisateur.getEmail());
        response.put("comptes", comptes);
        response.put("balance", balance);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/balance")
        public ResponseEntity<BigDecimal> getBalance(@PathVariable Integer id) {
        BigDecimal balance = service.getBalanceById(id);
        return ResponseEntity.ok(balance);  // Retourne la balance avec un code HTTP 200
    }
}
