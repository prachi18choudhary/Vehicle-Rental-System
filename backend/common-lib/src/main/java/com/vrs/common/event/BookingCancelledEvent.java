package com.vrs.common.event;

import java.io.Serializable;

public class BookingCancelledEvent implements Serializable {
    private Long bookingId;
    private Long userId;
    private String userEmail;
    private Long vehicleId;
    private String reason;

    public BookingCancelledEvent() {}

    public BookingCancelledEvent(Long bookingId, Long userId, String userEmail, Long vehicleId, String reason) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.vehicleId = vehicleId;
        this.reason = reason;
    }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
