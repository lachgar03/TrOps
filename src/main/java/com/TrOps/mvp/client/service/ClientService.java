package com.TrOps.mvp.client.service;

import com.TrOps.mvp.client.dto.ClientRequestDTO;
import com.TrOps.mvp.client.dto.ClientResponseDTO;
import com.TrOps.mvp.common.exception.ResourceNotFoundException;
import com.TrOps.mvp.client.model.Client;
import com.TrOps.mvp.client.repository.ClientRepository;
import com.TrOps.mvp.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    public ClientResponseDTO createClient(ClientRequestDTO request) {
        UUID companyId = getCurrentCompanyId();

        Client client = new Client();
        client.setCompanyId(companyId);
        client.setName(request.name());
        client.setEmail(request.email());
        client.setPhone(request.phone());

        return mapToResponseDTO(clientRepository.save(client));
    }

    public Page<ClientResponseDTO> getAllClients(Pageable pageable) {
        UUID companyId = getCurrentCompanyId();
        return clientRepository.findAllByCompanyId(companyId, pageable)
                .map(this::mapToResponseDTO);
    }

    public ClientResponseDTO getClientById(UUID id) {
        UUID companyId = getCurrentCompanyId();
        Client client = clientRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));
        return mapToResponseDTO(client);
    }

    @Transactional
    public ClientResponseDTO updateClient(UUID id, ClientRequestDTO request) {
        UUID companyId = getCurrentCompanyId();
        Client client = clientRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));

        client.setName(request.name());
        client.setEmail(request.email());
        client.setPhone(request.phone());

        return mapToResponseDTO(clientRepository.save(client));
    }

    @Transactional
    public void deleteClient(UUID id) {
        UUID companyId = getCurrentCompanyId();
        Client client = clientRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));
        clientRepository.delete(client);
    }

    // Used by MissionService to resolve a client by ID within the same tenant
    public Client resolveClient(UUID clientId, UUID companyId) {
        return clientRepository.findByIdAndCompanyId(clientId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable pour cette entreprise"));
    }

    private UUID getCurrentCompanyId() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getCompanyId();
    }

    private ClientResponseDTO mapToResponseDTO(Client client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getCreatedAt()
        );
    }
}
