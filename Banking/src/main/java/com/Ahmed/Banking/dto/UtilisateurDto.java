package com.Ahmed.Banking.dto;

import com.Ahmed.Banking.models.Utilisateur;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UtilisateurDto {

    // Champ ID
    private Integer id;

    @NotBlank(message = "Le nom ne peut pas être vide.")
    private String nom;

    @NotBlank(message = "Le prénom ne peut pas être vide.")
    private String prenom;

    @NotBlank(message = "L'email ne peut pas être vide.")
    @Email(message = "Format d'email invalide.")
    private String email;

    @NotBlank(message = "Le mot de passe ne peut pas être vide.")
    @Size(min = 8, max = 16, message = "Le mot de passe doit contenir entre 8 et 16 caractères.")
    private String motDePasse;

    // Conversion d'une entité Utilisateur en DTO
    public static UtilisateurDto fromEntity(Utilisateur utilisateur) {
        return UtilisateurDto.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .motDePasse(utilisateur.getMotDePasse())
                .build();
    }

    // Conversion d'un DTO en entité Utilisateur
    public static Utilisateur toEntity(UtilisateurDto dto) {
        return Utilisateur.builder()
                .id(dto.getId())
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .motDePasse(dto.getMotDePasse())
                .build();
    }
}
