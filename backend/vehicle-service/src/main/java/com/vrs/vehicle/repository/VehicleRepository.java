package com.vrs.vehicle.repository;

import com.vrs.vehicle.entity.Vehicle;
import com.vrs.vehicle.entity.VehicleStatus;
import com.vrs.vehicle.entity.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("""
        SELECT v FROM Vehicle v
        WHERE (:type IS NULL OR v.type = :type)
          AND (:location IS NULL OR LOWER(v.location) LIKE LOWER(CONCAT('%', :location, '%')))
          AND (:minPrice IS NULL OR v.pricePerDay >= :minPrice)
          AND (:maxPrice IS NULL OR v.pricePerDay <= :maxPrice)
          AND (:status IS NULL OR v.status = :status)
          AND (:q IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :q, '%'))
                          OR LOWER(v.brand) LIKE LOWER(CONCAT('%', :q, '%')))
        """)
    Page<Vehicle> search(
            @Param("q") String q,
            @Param("type") VehicleType type,
            @Param("location") String location,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("status") VehicleStatus status,
            Pageable pageable
    );
}
