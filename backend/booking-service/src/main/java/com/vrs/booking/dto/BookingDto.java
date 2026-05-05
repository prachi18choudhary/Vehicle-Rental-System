package com.vrs.booking.dto;

import com.vrs.booking.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingDto(
        Long id,
        Long userId,
        String userEmail,
        Long vehicleId,
        String vehicleName,
        LocalDateTime pickupAt,
        LocalDateTime dropoffAt,
        String pickupLocation,
        String dropoffLocation,
        BigDecimal totalAmount,
        Integer rentalDays,
        BookingStatus status,
        String cancelReason,
        LocalDateTime createdAt
) {}
