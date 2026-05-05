package com.vrs.booking.controller;

import com.vrs.booking.dto.BookingDto;
import com.vrs.booking.dto.BookingRequest;
import com.vrs.booking.entity.BookingStatus;
import com.vrs.booking.service.BookingService;
import com.vrs.common.security.AuthHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings")
public class BookingController {

    private final BookingService service;

    @PostMapping
    public ResponseEntity<BookingDto> create(
            @RequestHeader(AuthHeaders.USER_ID) Long userId,
            @RequestHeader(value = AuthHeaders.USER_EMAIL, required = false) String email,
            @Valid @RequestBody BookingRequest req) {
        return ResponseEntity.ok(service.create(userId, email, req));
    }

    @GetMapping("/me")
    public ResponseEntity<Page<BookingDto>> myBookings(
            @RequestHeader(AuthHeaders.USER_ID) Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(service.findMyBookings(userId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> get(
            @RequestHeader(AuthHeaders.USER_ID) Long userId,
            @RequestHeader(value = AuthHeaders.USER_ROLES, required = false) String roles,
            @PathVariable Long id) {
        boolean admin = roles != null && roles.contains("ROLE_ADMIN");
        return ResponseEntity.ok(service.findById(userId, id, admin));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingDto> cancel(
            @RequestHeader(AuthHeaders.USER_ID) Long userId,
            @RequestHeader(value = AuthHeaders.USER_ROLES, required = false) String roles,
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "User cancelled") : "User cancelled";
        boolean admin = roles != null && roles.contains("ROLE_ADMIN");
        return ResponseEntity.ok(service.cancel(userId, id, reason, admin));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingDto>> all(
            @RequestParam(required = false) BookingStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(service.findAll(status, pageable));
    }
}
