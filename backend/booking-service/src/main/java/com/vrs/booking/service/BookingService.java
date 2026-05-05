package com.vrs.booking.service;

import com.vrs.booking.client.VehicleClient;
import com.vrs.booking.client.dto.AvailabilityResponse;
import com.vrs.booking.dto.BookingDto;
import com.vrs.booking.dto.BookingRequest;
import com.vrs.booking.entity.Booking;
import com.vrs.booking.entity.BookingStatus;
import com.vrs.booking.event.BookingEventPublisher;
import com.vrs.booking.repository.BookingRepository;
import com.vrs.common.event.BookingCancelledEvent;
import com.vrs.common.event.BookingCreatedEvent;
import com.vrs.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository repo;
    private final VehicleClient vehicleClient;
    private final BookingEventPublisher publisher;

    @Transactional
    public BookingDto create(Long userId, String userEmail, BookingRequest req) {
        if (!req.dropoffAt().isAfter(req.pickupAt())) {
            throw ApiException.badRequest("Drop-off must be after pick-up");
        }

        AvailabilityResponse avail = vehicleClient.checkAvailability(req.vehicleId(), req.pickupAt(), req.dropoffAt());
        if (!avail.available()) {
            throw ApiException.badRequest("Vehicle not available: " + avail.reason());
        }

        List<Booking> overlaps = repo.findOverlappingBookings(req.vehicleId(), req.pickupAt(), req.dropoffAt());
        if (!overlaps.isEmpty()) {
            throw ApiException.conflict("Vehicle already booked for the selected time range");
        }

        Booking booking = Booking.builder()
                .userId(userId)
                .userEmail(userEmail)
                .vehicleId(req.vehicleId())
                .vehicleName(avail.vehicleName())
                .pickupAt(req.pickupAt())
                .dropoffAt(req.dropoffAt())
                .pickupLocation(req.pickupLocation())
                .dropoffLocation(req.dropoffLocation())
                .totalAmount(avail.totalAmount())
                .rentalDays((int) avail.days())
                .status(BookingStatus.PENDING_PAYMENT)
                .build();

        booking = repo.save(booking);

        BookingCreatedEvent event = new BookingCreatedEvent(
                booking.getId(), booking.getUserId(), booking.getUserEmail(),
                booking.getVehicleId(), booking.getVehicleName(),
                booking.getPickupAt(), booking.getDropoffAt(),
                booking.getPickupLocation(), booking.getDropoffLocation(),
                booking.getTotalAmount());
        publisher.publishBookingCreated(event);

        return toDto(booking);
    }

    @Transactional
    public BookingDto cancel(Long userId, Long bookingId, String reason, boolean isAdmin) {
        Booking b = repo.findById(bookingId).orElseThrow(() -> ApiException.notFound("Booking not found"));
        if (!isAdmin && !b.getUserId().equals(userId)) {
            throw ApiException.forbidden("Cannot cancel another user's booking");
        }
        if (b.getStatus() == BookingStatus.CANCELLED || b.getStatus() == BookingStatus.COMPLETED) {
            throw ApiException.badRequest("Booking cannot be cancelled in current status");
        }
        b.setStatus(BookingStatus.CANCELLED);
        b.setCancelReason(reason);
        repo.save(b);

        publisher.publishBookingCancelled(new BookingCancelledEvent(
                b.getId(), b.getUserId(), b.getUserEmail(), b.getVehicleId(), reason));
        return toDto(b);
    }

    @Transactional(readOnly = true)
    public BookingDto findById(Long userId, Long bookingId, boolean isAdmin) {
        Booking b = repo.findById(bookingId).orElseThrow(() -> ApiException.notFound("Booking not found"));
        if (!isAdmin && !b.getUserId().equals(userId)) {
            throw ApiException.forbidden("Cannot view another user's booking");
        }
        return toDto(b);
    }

    @Transactional(readOnly = true)
    public Page<BookingDto> findMyBookings(Long userId, Pageable pageable) {
        return repo.findByUserId(userId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<BookingDto> findAll(BookingStatus status, Pageable pageable) {
        return (status == null ? repo.findAll(pageable) : repo.findByStatus(status, pageable)).map(this::toDto);
    }

    /** Auto-cancel bookings that stayed in PENDING_PAYMENT for too long. */
    @Transactional
    public int cancelStalePendingPayments(int minutesOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(minutesOld);
        List<Booking> stale = repo.findStalePendingPayments(cutoff);
        for (Booking b : stale) {
            b.setStatus(BookingStatus.CANCELLED);
            b.setCancelReason("Payment not completed in time");
            repo.save(b);
            publisher.publishBookingCancelled(new BookingCancelledEvent(
                    b.getId(), b.getUserId(), b.getUserEmail(), b.getVehicleId(), b.getCancelReason()));
        }
        if (!stale.isEmpty()) log.info("Auto-cancelled {} stale pending bookings", stale.size());
        return stale.size();
    }

    private BookingDto toDto(Booking b) {
        return new BookingDto(b.getId(), b.getUserId(), b.getUserEmail(), b.getVehicleId(), b.getVehicleName(),
                b.getPickupAt(), b.getDropoffAt(), b.getPickupLocation(), b.getDropoffLocation(),
                b.getTotalAmount(), b.getRentalDays(), b.getStatus(), b.getCancelReason(), b.getCreatedAt());
    }
}
