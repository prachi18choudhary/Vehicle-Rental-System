package com.vrs.payment.repository;

import com.vrs.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long bookingId);
    Optional<Payment> findByRazorpayOrderId(String orderId);
    Page<Payment> findByUserId(Long userId, Pageable pageable);
}
