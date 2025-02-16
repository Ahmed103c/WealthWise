package com.Ahmed.Banking.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;



import java.util.Date;

@Component
public class JwtTokenProvider {

    // Définir la clé secrète pour signer le JWT
    private String SECRET_KEY = "bembliwiem2025/2026longsecurekeyforjwt1234567891011"; // Remplace cette valeur par une clé plus sécurisée en production

    private long validityInMilliseconds = 3600000L; // 1 heure

    // Méthode pour générer le token JWT
    public String generateToken(String email) {
        try {
            String token = Jwts.builder()
                    .setSubject(email)  // L'email est mis comme "subject" dans le token
                    .setIssuedAt(new Date())  // Date de création du token
                    .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds))  // Date d'expiration
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY)  // Utilise la clé secrète et l'algorithme HS256
                    .compact();  // Génére le token

            // Log pour vérifier que le token est bien généré
            System.out.println("Generated Token: " + token);

            return token;
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);  // Lance une exception en cas d'erreur
        }
    }

    // Méthode pour valider un token JWT
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)  // Utilise la clé secrète pour valider le token
                    .build()
                    .parseClaimsJws(token);
            return true; // Le token est valide
        } catch (Exception e) {
            return false; // Le token n'est pas valide
        }
    }

    // Méthode pour obtenir le nom d'utilisateur (email) du token JWT
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)  // Utilise la clé secrète pour extraire les informations
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject(); // Retourne l'email du sujet
    }
}
