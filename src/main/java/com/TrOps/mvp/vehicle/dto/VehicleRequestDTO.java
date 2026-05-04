package com.TrOps.mvp.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VehicleRequestDTO(
        @NotBlank(message = "Registration number is required") String registrationNumber,
        @NotBlank(message = "Brand is required") String brand,
        @NotBlank(message = "Model is required") String model,
        @NotNull(message = "Current mileage is required") Integer currentMileage
) {
}
