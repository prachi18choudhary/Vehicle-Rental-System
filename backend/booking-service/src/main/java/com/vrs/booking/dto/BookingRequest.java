package com.vrs.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookingRequest(
        @NotNull Long vehicleId,
        @NotNull @Future LocalDateTime pickupAt,
        @NotNull @Future LocalDateTime dropoffAt,
        @NotBlank String pickupLocation,
        @NotBlank String dropoffLocation
) {}
