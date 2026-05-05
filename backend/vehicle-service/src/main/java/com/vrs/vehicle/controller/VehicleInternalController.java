package com.vrs.vehicle.controller;

import com.vrs.vehicle.entity.VehicleStatus;
import com.vrs.vehicle.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Internal endpoint for service-to-service vehicle status updates.
 * Used by booking-service when RabbitMQ is disabled (norabbitmq profile).
 */
@RestController
@RequestMapping("/vehicles/internal")
@RequiredArgsConstructor
@Slf4j
public class VehicleInternalController {

    private final VehicleService service;

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam VehicleStatus status) {
        log.info("Internal vehicle status update: vehicleId={}, status={}", id, status);
        service.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
