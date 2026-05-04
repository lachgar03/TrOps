package com.TrOps.mvp.common.config;

import com.TrOps.mvp.client.model.Client;
import com.TrOps.mvp.client.repository.ClientRepository;
import com.TrOps.mvp.company.repository.CompanyRepository;
import com.TrOps.mvp.vehicle.model.Vehicle;
import com.TrOps.mvp.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DevDataInitializer implements CommandLineRunner {

    private final ClientRepository clientRepository;
    private final VehicleRepository vehicleRepository;
    private final CompanyRepository companyRepository;

    @Override
    public void run(String... args) {
        // On récupère la première entreprise existante (celle que vous avez créée tout à l'heure)
        companyRepository.findAll().stream().findFirst().ifPresent(company -> {
            
            if (clientRepository.count() == 0) {
                Client client = new Client();
                client.setName("Client Test (Acme Corp)");
                client.setCompanyId(company.getId());
                client = clientRepository.save(client);
                System.out.println("\n=======================================");
                System.out.println("🎯 TEST CLIENT ID: " + client.getId());
                System.out.println("=======================================\n");
            }
            
            if (vehicleRepository.count() == 0) {
                Vehicle vehicle = new Vehicle();
                vehicle.setRegistrationNumber("12345-A-6");
                vehicle.setUnderMaintenance(false);
                vehicle.setCompanyId(company.getId());
                vehicle = vehicleRepository.save(vehicle);
                System.out.println("\n=======================================");
                System.out.println("🚚 TEST VEHICLE ID: " + vehicle.getId());
                System.out.println("=======================================\n");
            }
        });
    }
}
