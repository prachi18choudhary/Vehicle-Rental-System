package com.vrs.vehicle.controller;

import com.vrs.vehicle.dto.AvailabilityResponse;
import com.vrs.vehicle.dto.VehicleDto;
import com.vrs.vehicle.dto.VehicleRequest;
import com.vrs.vehicle.entity.VehicleStatus;
import com.vrs.vehicle.entity.VehicleType;
import com.vrs.vehicle.service.VehicleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles")
public class VehicleController {

    private final VehicleService service;

    @GetMapping
    public ResponseEntity<Page<VehicleDto>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) VehicleType type,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) VehicleStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(service.search(q, type, location, minPrice, maxPrice, status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponse> availability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime pickupAt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dropoffAt) {
        return ResponseEntity.ok(service.checkAvailability(id, pickupAt, dropoffAt));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleDto> create(@Valid @RequestBody VehicleRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleDto> update(@PathVariable Long id, @Valid @RequestBody VehicleRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleDto> updateStatus(@PathVariable Long id, @RequestParam VehicleStatus status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
