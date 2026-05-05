package com.vrs.common.event;

import java.io.Serializable;
import java.math.BigDecimal;

public class PaymentEvent implements Serializable {
    private Long paymentId;
    private Long bookingId;
    private Long userId;
    private String userEmail;
    private String razorpayPaymentId;
    private BigDecimal amount;
    private String status;

    public PaymentEvent() {}

    public PaymentEvent(Long paymentId, Long bookingId, Long userId, String userEmail,
                        String razorpayPaymentId, BigDecimal amount, String status) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.razorpayPaymentId = razorpayPaymentId;
        this.amount = amount;
        this.status = status;
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
