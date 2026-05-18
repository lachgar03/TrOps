package com.TrOps.mvp.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {
    public record LoginRequest(
            @NotBlank(message = "L'email est obligatoire")
            @Email(message = "Format d'email invalide")
            String email,
            @NotBlank(message = "Le mot de passe est obligatoire")
            String password
    ) {}

    public record RegisterCompanyRequest(
            @NotBlank(message = "Le nom de l'entreprise est obligatoire")
            @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
            String companyName,
            @NotBlank(message = "Le prénom est obligatoire")
            String adminFirstName,
            @NotBlank(message = "Le nom est obligatoire")
            String adminLastName,
            @NotBlank(message = "L'email est obligatoire")
            @Email(message = "Format d'email invalide")
            String adminEmail,
            @NotBlank(message = "Le mot de passe est obligatoire")
            @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
            String adminPassword
    ) {}

    public record JwtResponse(String token) {}
}
