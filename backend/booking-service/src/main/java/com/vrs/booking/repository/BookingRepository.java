package com.vrs.booking.repository;

import com.vrs.booking.entity.Booking;
import com.vrs.booking.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByUserId(Long userId, Pageable pageable);
    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.vehicleId = :vehicleId
          AND b.status IN ('PENDING_PAYMENT','CONFIRMED')
          AND b.pickupAt < :dropoff
          AND b.dropoffAt > :pickup
    """)
    List<Booking> findOverlappingBookings(@Param("vehicleId") Long vehicleId,
                                          @Param("pickup") LocalDateTime pickup,
                                          @Param("dropoff") LocalDateTime dropoff);

    @Query("SELECT b FROM Booking b WHERE b.status = 'PENDING_PAYMENT' AND b.createdAt < :cutoff")
    List<Booking> findStalePendingPayments(@Param("cutoff") LocalDateTime cutoff);
}
