package com.vrs.payment.controller;

import com.vrs.common.security.AuthHeaders;
import com.vrs.payment.dto.*;
import com.vrs.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payments")
public class PaymentController {

    private final PaymentService service;

    @PostMapping("/order")
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestHeader(AuthHeaders.USER_ID) Long userId,
            @RequestHeader(value = AuthHeaders.USER_EMAIL, required = false) String email,
            @Valid @RequestBody CreateOrderRequest req) {
        return ResponseEntity.ok(service.createOrder(userId, email, req));
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentDto> verify(
            @RequestHeader(AuthHeaders.USER_ID) Long userId,
            @Valid @RequestBody VerifyRequest req) {
        return ResponseEntity.ok(service.verify(userId, req));
    }

    @PostMapping("/{id}/fail")
    public ResponseEntity<PaymentDto> fail(
            @RequestHeader(AuthHeaders.USER_ID) Long userId,
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "Cancelled by user") : "Cancelled by user";
        return ResponseEntity.ok(service.markFailed(userId, id, reason));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentDto> byBooking(
            @RequestHeader(AuthHeaders.USER_ID) Long userId,
            @RequestHeader(value = AuthHeaders.USER_ROLES, required = false) String roles,
            @PathVariable Long bookingId) {
        boolean admin = roles != null && roles.contains("ROLE_ADMIN");
        return ResponseEntity.ok(service.findByBooking(userId, bookingId, admin));
    }

    @GetMapping("/me")
    public ResponseEntity<Page<PaymentDto>> myPayments(
            @RequestHeader(AuthHeaders.USER_ID) Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(service.findByUser(userId, pageable));
    }
}
