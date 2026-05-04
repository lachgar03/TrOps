package com.TrOps.mvp.security.service;

import com.TrOps.mvp.company.model.Company;
import com.TrOps.mvp.company.repository.CompanyRepository;
import com.TrOps.mvp.security.dto.AuthDtos;
import com.TrOps.mvp.security.jwt.JwtUtils;
import com.TrOps.mvp.user.model.Role;
import com.TrOps.mvp.user.model.User;
import com.TrOps.mvp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthDtos.JwtResponse registerCompany(AuthDtos.RegisterCompanyRequest request) {
        if (companyRepository.existsByName(request.companyName())) {
            throw new IllegalArgumentException("Une entreprise avec ce nom existe déjà");
        }
        if (userRepository.existsByEmail(request.adminEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        // 1. Créer la compagnie
        Company company = new Company();
        company.setName(request.companyName());
        Company savedCompany = companyRepository.save(company);

        // 2. Créer l'utilisateur administrateur
        User admin = new User();
        admin.setFirstName(request.adminFirstName());
        admin.setLastName(request.adminLastName());
        admin.setEmail(request.adminEmail());
        admin.setPassword(passwordEncoder.encode(request.adminPassword()));
        admin.setRole(Role.ADMIN);
        admin.setCompanyId(savedCompany.getId());
        userRepository.save(admin);

        // 3. Générer le token
        String jwtToken = jwtUtils.generateToken(admin);
        return new AuthDtos.JwtResponse(jwtToken);
    }

    public AuthDtos.JwtResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(); // Should not happen since authenticate passed
                
        String jwtToken = jwtUtils.generateToken(user);
        return new AuthDtos.JwtResponse(jwtToken);
    }
}
