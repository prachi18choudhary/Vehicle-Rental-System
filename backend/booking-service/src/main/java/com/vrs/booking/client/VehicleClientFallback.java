package com.vrs.booking.client;

import com.vrs.booking.client.dto.AvailabilityResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class VehicleClientFallback implements VehicleClient {
    @Override
    public AvailabilityResponse checkAvailability(Long id, LocalDateTime pickupAt, LocalDateTime dropoffAt) {
        return new AvailabilityResponse(id, false, null, null, 0, null, "Vehicle service unavailable");
    }

    @Override
    public void updateStatus(Long id, String status) {
        // Fallback: do nothing or log
    }
}
