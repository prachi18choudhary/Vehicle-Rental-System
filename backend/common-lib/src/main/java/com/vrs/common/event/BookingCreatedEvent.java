package com.vrs.common.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookingCreatedEvent implements Serializable {
    private Long bookingId;
    private Long userId;
    private String userEmail;
    private Long vehicleId;
    private String vehicleName;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime pickupAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime dropoffAt;
    private String pickupLocation;
    private String dropoffLocation;
    private BigDecimal totalAmount;

    public BookingCreatedEvent() {}

    public BookingCreatedEvent(Long bookingId, Long userId, String userEmail, Long vehicleId, String vehicleName,
                               LocalDateTime pickupAt, LocalDateTime dropoffAt,
                               String pickupLocation, String dropoffLocation, BigDecimal totalAmount) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.pickupAt = pickupAt;
        this.dropoffAt = dropoffAt;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.totalAmount = totalAmount;
    }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    public String getVehicleName() { return vehicleName; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }
    public LocalDateTime getPickupAt() { return pickupAt; }
    public void setPickupAt(LocalDateTime pickupAt) { this.pickupAt = pickupAt; }
    public LocalDateTime getDropoffAt() { return dropoffAt; }
    public void setDropoffAt(LocalDateTime dropoffAt) { this.dropoffAt = dropoffAt; }
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public String getDropoffLocation() { return dropoffLocation; }
    public void setDropoffLocation(String dropoffLocation) { this.dropoffLocation = dropoffLocation; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
