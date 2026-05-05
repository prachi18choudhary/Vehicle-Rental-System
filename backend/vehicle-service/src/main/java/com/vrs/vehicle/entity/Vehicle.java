package com.vrs.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 60)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VehicleType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Transmission transmission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FuelType fuel;

    @Column(nullable = false)
    private Integer seats;

    @Column(name = "price_per_day", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    @Column(nullable = false, length = 100)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleStatus status;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String description;

    @Column(name = "license_plate", length = 30, unique = true)
    private String licensePlate;

    @Column(name = "year_made")
    private Integer yearMade;

    @Column(name = "rating", precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = VehicleStatus.AVAILABLE;
        if (this.rating == null) this.rating = BigDecimal.valueOf(4.5);
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
