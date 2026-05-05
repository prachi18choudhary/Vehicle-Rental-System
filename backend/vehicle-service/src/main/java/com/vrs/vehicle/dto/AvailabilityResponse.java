package com.vrs.vehicle.dto;

import java.math.BigDecimal;

public record AvailabilityResponse(
        Long vehicleId,
        boolean available,
        BigDecimal pricePerDay,
        BigDecimal totalAmount,
        long days,
        String vehicleName,
        String reason
) {}
