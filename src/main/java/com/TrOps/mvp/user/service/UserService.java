package com.TrOps.mvp.user.service;

import com.TrOps.mvp.common.exception.ResourceNotFoundException;
import com.TrOps.mvp.user.dto.UserCreateDTO;
import com.TrOps.mvp.user.dto.UserResponseDTO;
import com.TrOps.mvp.user.model.User;
import com.TrOps.mvp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO createUser(UserCreateDTO request) {
        UUID companyId = getCurrentCompanyId();

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        User user = new User();
        user.setCompanyId(companyId);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());

        return mapToResponseDTO(userRepository.save(user));
    }

    public List<UserResponseDTO> getAllUsers() {
        UUID companyId = getCurrentCompanyId();
        return userRepository.findAllByCompanyId(companyId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public UserResponseDTO getUserById(UUID id) {
        UUID companyId = getCurrentCompanyId();
        User user = userRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return mapToResponseDTO(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        UUID companyId = getCurrentCompanyId();
        User user = userRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        userRepository.delete(user);
    }

    private UUID getCurrentCompanyId() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getCompanyId();
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
