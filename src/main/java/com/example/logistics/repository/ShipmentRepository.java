package com.example.logistics.repository;

import com.example.logistics.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    boolean existsByTrackingNumber(String trackingNumber);
}