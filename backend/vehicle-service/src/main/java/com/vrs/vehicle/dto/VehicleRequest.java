package com.vrs.vehicle.dto;

import com.vrs.vehicle.entity.FuelType;
import com.vrs.vehicle.entity.Transmission;
import com.vrs.vehicle.entity.VehicleType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record VehicleRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 60) String brand,
        @NotNull VehicleType type,
        @NotNull Transmission transmission,
        @NotNull FuelType fuel,
        @NotNull @Min(1) Integer seats,
        @NotNull @DecimalMin("0.01") BigDecimal pricePerDay,
        @NotBlank @Size(max = 100) String location,
        @Size(max = 500) String imageUrl,
        @Size(max = 500) String description,
        @Size(max = 30) String licensePlate,
        @Min(1900) Integer yearMade
) {}
