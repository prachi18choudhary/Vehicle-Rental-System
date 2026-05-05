package com.vrs.payment.service;

import com.razorpay.Order;
import com.vrs.common.event.PaymentEvent;
import com.vrs.common.event.RabbitConfig;
import com.vrs.common.exception.ApiException;
import com.vrs.payment.dto.CreateOrderRequest;
import com.vrs.payment.dto.CreateOrderResponse;
import com.vrs.payment.dto.PaymentDto;
import com.vrs.payment.dto.VerifyRequest;
import com.vrs.payment.entity.Payment;
import com.vrs.payment.entity.PaymentStatus;
import com.vrs.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.vrs.payment.event.PaymentEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository repo;
    private final RazorpayService razorpay;
    private final PaymentEventPublisher publisher;

    @Transactional
    public CreateOrderResponse createOrder(Long userId, String userEmail, CreateOrderRequest req) {
        Payment existing = repo.findByBookingId(req.bookingId()).orElse(null);
        if (existing != null && existing.getStatus() == PaymentStatus.SUCCESS) {
            throw ApiException.conflict("Booking already paid");
        }

        Order order = razorpay.createOrder(req.amount(), "rcpt_booking_" + req.bookingId());
        String orderId = order.get("id");

        Payment payment = existing != null ? existing : Payment.builder()
                .bookingId(req.bookingId())
                .userId(userId)
                .userEmail(userEmail)
                .amount(req.amount())
                .currency(razorpay.getCurrency())
                .status(PaymentStatus.CREATED)
                .build();
        payment.setRazorpayOrderId(orderId);
        payment.setStatus(PaymentStatus.CREATED);
        payment.setAmount(req.amount());
        payment.setUserId(userId);
        payment.setUserEmail(userEmail);
        payment = repo.save(payment);

        return new CreateOrderResponse(payment.getId(), payment.getBookingId(), orderId,
                razorpay.getKeyId(), payment.getAmount(), payment.getCurrency());
    }

    @Transactional
    public PaymentDto verify(Long userId, VerifyRequest req) {
        Payment payment = repo.findByRazorpayOrderId(req.razorpayOrderId())
                .orElseThrow(() -> ApiException.notFound("Order not found"));

        if (!payment.getUserId().equals(userId)) {
            throw ApiException.forbidden("Cannot verify another user's payment");
        }

        boolean ok = razorpay.verifySignature(req.razorpayOrderId(), req.razorpayPaymentId(), req.razorpaySignature());
        payment.setRazorpayPaymentId(req.razorpayPaymentId());
        payment.setRazorpaySignature(req.razorpaySignature());
        payment.setStatus(ok ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        if (!ok) payment.setReason("Invalid signature");
        payment = repo.save(payment);

        PaymentEvent event = new PaymentEvent(payment.getId(), payment.getBookingId(), payment.getUserId(),
                payment.getUserEmail(), req.razorpayPaymentId(), payment.getAmount(),
                ok ? "SUCCESS" : "FAILED");
        publisher.publishPaymentEvent(event, ok);
        log.info("Payment {} for booking {}: {}", payment.getId(), payment.getBookingId(), ok ? "SUCCESS" : "FAILED");

        return toDto(payment);
    }

    @Transactional
    public PaymentDto markFailed(Long userId, Long paymentId, String reason) {
        Payment payment = repo.findById(paymentId).orElseThrow(() -> ApiException.notFound("Payment not found"));
        if (!payment.getUserId().equals(userId)) {
            throw ApiException.forbidden("Cannot fail another user's payment");
        }
        payment.setStatus(PaymentStatus.FAILED);
        payment.setReason(reason);
        payment = repo.save(payment);

        PaymentEvent event = new PaymentEvent(payment.getId(), payment.getBookingId(), payment.getUserId(),
                payment.getUserEmail(), payment.getRazorpayPaymentId(), payment.getAmount(), "FAILED");
        publisher.publishPaymentEvent(event, false);
        return toDto(payment);
    }

    @Transactional(readOnly = true)
    public PaymentDto findByBooking(Long userId, Long bookingId, boolean isAdmin) {
        Payment p = repo.findByBookingId(bookingId).orElseThrow(() -> ApiException.notFound("Payment not found"));
        if (!isAdmin && !p.getUserId().equals(userId)) {
            throw ApiException.forbidden("Forbidden");
        }
        return toDto(p);
    }

    @Transactional(readOnly = true)
    public Page<PaymentDto> findByUser(Long userId, Pageable pageable) {
        return repo.findByUserId(userId, pageable).map(this::toDto);
    }

    private PaymentDto toDto(Payment p) {
        return new PaymentDto(p.getId(), p.getBookingId(), p.getUserId(),
                p.getRazorpayOrderId(), p.getRazorpayPaymentId(), p.getAmount(), p.getCurrency(),
                p.getStatus(), p.getCreatedAt());
    }
}
