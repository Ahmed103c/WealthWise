package com.Ahmed.Banking.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Ahmed.Banking.dto.UtilisateurDto;
import com.Ahmed.Banking.services.UtilisateurService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService service;

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

    // API de login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        try {
            // Chercher l'utilisateur par email
            UtilisateurDto utilisateur = service.findByMail(email);

            // Vérifier le mot de passe (ici, tu devras adapter selon ta logique de validation du mot de passe)
            if (isPasswordValid(password, utilisateur.getPassword())) {
                // Si le mot de passe est valide, retourner un succès
                return ResponseEntity.ok("Login successful");
            } else {
                // Si le mot de passe est incorrect
                return ResponseEntity.status(401).body("Invalid password");
            }
        } catch (Exception e) {
            // Si l'utilisateur n'est pas trouvé
            return ResponseEntity.status(404).body("User not found with the email: " + email);
        }
    }

    // Méthode pour vérifier si le mot de passe est valide (à adapter selon ta logique de hachage)
    private boolean isPasswordValid(String inputPassword, String storedPassword) {
        // Utiliser par exemple BCrypt pour comparer le mot de passe
        return inputPassword.equals(storedPassword);  // Remplace par la méthode de validation adéquate (par exemple, BCrypt)
    }
}

