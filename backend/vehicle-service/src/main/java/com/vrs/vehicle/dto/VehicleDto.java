package com.vrs.vehicle.dto;

import com.vrs.vehicle.entity.FuelType;
import com.vrs.vehicle.entity.Transmission;
import com.vrs.vehicle.entity.VehicleStatus;
import com.vrs.vehicle.entity.VehicleType;

import java.math.BigDecimal;

public record VehicleDto(
        Long id,
        String name,
        String brand,
        VehicleType type,
        Transmission transmission,
        FuelType fuel,
        Integer seats,
        BigDecimal pricePerDay,
        String location,
        VehicleStatus status,
        String imageUrl,
        String description,
        String licensePlate,
        Integer yearMade,
        BigDecimal rating
) {}
