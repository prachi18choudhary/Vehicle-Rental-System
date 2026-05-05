package com.vrs.vehicle.service;

import com.vrs.common.exception.ApiException;
import com.vrs.vehicle.dto.AvailabilityResponse;
import com.vrs.vehicle.dto.VehicleDto;
import com.vrs.vehicle.dto.VehicleRequest;
import com.vrs.vehicle.entity.Vehicle;
import com.vrs.vehicle.entity.VehicleStatus;
import com.vrs.vehicle.entity.VehicleType;
import com.vrs.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository repo;

    @Transactional
    public VehicleDto create(VehicleRequest req) {
        Vehicle v = Vehicle.builder()
                .name(req.name())
                .brand(req.brand())
                .type(req.type())
                .transmission(req.transmission())
                .fuel(req.fuel())
                .seats(req.seats())
                .pricePerDay(req.pricePerDay())
                .location(req.location())
                .imageUrl(req.imageUrl())
                .description(req.description())
                .licensePlate(req.licensePlate())
                .yearMade(req.yearMade())
                .status(VehicleStatus.AVAILABLE)
                .build();
        return toDto(repo.save(v));
    }

    @Transactional
    public VehicleDto update(Long id, VehicleRequest req) {
        Vehicle v = repo.findById(id).orElseThrow(() -> ApiException.notFound("Vehicle not found"));
        v.setName(req.name());
        v.setBrand(req.brand());
        v.setType(req.type());
        v.setTransmission(req.transmission());
        v.setFuel(req.fuel());
        v.setSeats(req.seats());
        v.setPricePerDay(req.pricePerDay());
        v.setLocation(req.location());
        v.setImageUrl(req.imageUrl());
        v.setDescription(req.description());
        v.setLicensePlate(req.licensePlate());
        v.setYearMade(req.yearMade());
        return toDto(repo.save(v));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw ApiException.notFound("Vehicle not found");
        repo.deleteById(id);
    }

    @Transactional
    public VehicleDto updateStatus(Long id, VehicleStatus status) {
        Vehicle v = repo.findById(id).orElseThrow(() -> ApiException.notFound("Vehicle not found"));
        v.setStatus(status);
        return toDto(repo.save(v));
    }

    @Transactional(readOnly = true)
    public VehicleDto findById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> ApiException.notFound("Vehicle not found")));
    }

    @Transactional(readOnly = true)
    public Page<VehicleDto> search(String q, VehicleType type, String location,
                                   BigDecimal minPrice, BigDecimal maxPrice,
                                   VehicleStatus status, Pageable pageable) {
        return repo.search(q, type, location, minPrice, maxPrice, status, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public AvailabilityResponse checkAvailability(Long vehicleId, LocalDateTime pickupAt, LocalDateTime dropoffAt) {
        Vehicle v = repo.findById(vehicleId).orElse(null);
        if (v == null) {
            return new AvailabilityResponse(vehicleId, false, null, null, 0, null, "Vehicle not found");
        }
        if (pickupAt == null || dropoffAt == null || !dropoffAt.isAfter(pickupAt)) {
            return new AvailabilityResponse(vehicleId, false, v.getPricePerDay(), null, 0, v.getName(),
                    "Invalid date range");
        }
        if (v.getStatus() != VehicleStatus.AVAILABLE) {
            return new AvailabilityResponse(vehicleId, false, v.getPricePerDay(), null, 0, v.getName(),
                    "Vehicle is " + v.getStatus());
        }
        long hours = Duration.between(pickupAt, dropoffAt).toHours();
        long days = Math.max(1, (long) Math.ceil(hours / 24.0));
        BigDecimal total = v.getPricePerDay().multiply(BigDecimal.valueOf(days));
        return new AvailabilityResponse(vehicleId, true, v.getPricePerDay(), total, days, v.getName(), null);
    }

    private VehicleDto toDto(Vehicle v) {
        return new VehicleDto(v.getId(), v.getName(), v.getBrand(), v.getType(), v.getTransmission(),
                v.getFuel(), v.getSeats(), v.getPricePerDay(), v.getLocation(), v.getStatus(),
                v.getImageUrl(), v.getDescription(), v.getLicensePlate(), v.getYearMade(), v.getRating());
    }
}
