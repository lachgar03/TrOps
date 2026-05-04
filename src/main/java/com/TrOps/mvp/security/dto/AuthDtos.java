package com.TrOps.mvp.security.dto;

public class AuthDtos {
    public record LoginRequest(String email, String password) {}
    
    public record RegisterCompanyRequest(
            String companyName, 
            String adminFirstName, 
            String adminLastName, 
            String adminEmail, 
            String adminPassword
    ) {}
    
    public record JwtResponse(String token) {}
}
