package com.vrs.payment.dto;

import java.math.BigDecimal;

public record CreateOrderResponse(
        Long paymentId,
        Long bookingId,
        String razorpayOrderId,
        String razorpayKeyId,
        BigDecimal amount,
        String currency
) {}
