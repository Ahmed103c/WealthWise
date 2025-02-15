package com.Ahmed.Banking.dto;

import com.Ahmed.Banking.models.Utilisateur;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UtilisateurDto {

    @NotNull
    @NotEmpty
    @NotBlank(message = "Nom_is_Blank ! ")
    private String nom;
    @NotNull
    @NotEmpty
    @NotBlank
    private String prenom;
    @NotNull
    @NotEmpty
    @NotBlank
    @Email
    private String email;
    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min=8,max=16)
    private String motDePasse;

    //transformation userEntity ---> userDto
    public static UtilisateurDto fromEntity(Utilisateur utilisateur)
    {
        return UtilisateurDto.builder()
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .motDePasse(utilisateur.getMotDePasse())
                .build();
    }

    //transformation userDto ---> userEntity
    public static Utilisateur toEntity(UtilisateurDto utilisateur)
    {
        return Utilisateur.builder()
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .motDePasse(utilisateur.getMotDePasse())
                .build();
    }
}
