package com.vrs.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_user", columnList = "user_id"),
        @Index(name = "idx_vehicle", columnList = "vehicle_id"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_email", length = 150)
    private String userEmail;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "vehicle_name", length = 100)
    private String vehicleName;

    @Column(name = "pickup_at", nullable = false)
    private LocalDateTime pickupAt;

    @Column(name = "dropoff_at", nullable = false)
    private LocalDateTime dropoffAt;

    @Column(name = "pickup_location", length = 200)
    private String pickupLocation;

    @Column(name = "dropoff_location", length = 200)
    private String dropoffLocation;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "rental_days", nullable = false)
    private Integer rentalDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingStatus status;

    @Column(name = "cancel_reason", length = 300)
    private String cancelReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = BookingStatus.PENDING_PAYMENT;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
