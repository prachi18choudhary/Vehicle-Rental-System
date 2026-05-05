package com.vrs.payment.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotNull Long bookingId,
        @NotNull BigDecimal amount
) {}
