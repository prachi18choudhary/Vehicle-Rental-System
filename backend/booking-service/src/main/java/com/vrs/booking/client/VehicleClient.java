package com.vrs.booking.client;

import com.vrs.booking.client.dto.AvailabilityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@FeignClient(name = "vehicle-service", fallback = VehicleClientFallback.class)
public interface VehicleClient {

    @GetMapping("/vehicles/{id}/availability")
    AvailabilityResponse checkAvailability(
            @PathVariable("id") Long id,
            @RequestParam("pickupAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime pickupAt,
            @RequestParam("dropoffAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dropoffAt
    );

    @org.springframework.web.bind.annotation.PatchMapping("/vehicles/internal/{id}/status")
    void updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status);
}
