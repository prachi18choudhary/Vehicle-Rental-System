package com.vrs.payment.dto;

import com.vrs.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentDto(
        Long id,
        Long bookingId,
        Long userId,
        String razorpayOrderId,
        String razorpayPaymentId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        LocalDateTime createdAt
) {}
