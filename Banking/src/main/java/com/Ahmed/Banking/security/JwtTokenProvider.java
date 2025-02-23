package com.Ahmed.Banking.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // Clé secrète pour signer le JWT – à sécuriser en production
    private String SECRET_KEY = "bembliwiem2025/2026longsecurekeyforjwt1234567891011";

    private long validityInMilliseconds = 3600000L; // 1 heure

    // Génère le token JWT en incluant l'email et l'ID utilisateur
    public String generateToken(String email, Integer userId) {
        try {
            String token = Jwts.builder()
                    .setSubject(email)                        // L'email comme "subject"
                    .claim("userId", userId)                   // Ajout du claim userId
                    .setIssuedAt(new Date())                   // Date de création du token
                    .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds))  // Date d'expiration
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY)  // Signature
                    .compact();
            System.out.println("Generated Token: " + token);
            return token;
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    // Valide le token JWT
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extrait l'email (subject) du token JWT
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // Extrait l'ID utilisateur du token JWT
    public Integer getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", Integer.class);  // 🔹 Extraction directe de l'ID utilisateur
    }
}
