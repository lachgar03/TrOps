package com.TrOps.mvp.security.controller;

import com.TrOps.mvp.security.dto.AuthDtos;
import com.TrOps.mvp.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentification et enregistrement")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register-company")
    @Operation(summary = "Enregistrer une nouvelle entreprise avec son administrateur")
    public ResponseEntity<AuthDtos.JwtResponse> registerCompany(
            @Valid @RequestBody AuthDtos.RegisterCompanyRequest request) {
        return ResponseEntity.ok(authService.registerCompany(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Se connecter et obtenir un token JWT")
    public ResponseEntity<AuthDtos.JwtResponse> login(
            @Valid @RequestBody AuthDtos.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

