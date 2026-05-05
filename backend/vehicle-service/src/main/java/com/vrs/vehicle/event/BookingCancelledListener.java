package com.vrs.vehicle.event;

import com.vrs.common.event.BookingCancelledEvent;
import com.vrs.common.event.RabbitConfig;
import com.vrs.vehicle.entity.VehicleStatus;
import com.vrs.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class BookingCancelledListener {

    private final VehicleRepository vehicleRepo;

    @RabbitListener(queues = RabbitConfig.Q_VEHICLE_BOOKING_CANCELLED)
    @Transactional
    public void onBookingCancelled(BookingCancelledEvent event) {
        log.info("Vehicle service received booking.cancelled for vehicleId={}", event.getVehicleId());
        vehicleRepo.findById(event.getVehicleId()).ifPresent(v -> {
            if (v.getStatus() == VehicleStatus.RENTED) {
                v.setStatus(VehicleStatus.AVAILABLE);
                vehicleRepo.save(v);
            }
        });
    }
}
