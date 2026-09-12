package com.example.logistics.repository;

import com.example.logistics.entity.ShipmentException;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExceptionRepository extends JpaRepository<ShipmentException, Long> {
    List<ShipmentException> findByStatus(ShipmentException.ExceptionStatus status);
    List<ShipmentException> findByPriority(ShipmentException.Priority priority);
    List<ShipmentException> findByType(ShipmentException.ExceptionType type);
    List<ShipmentException> findByAssignedToId(Long userId);
    List<ShipmentException> findByShipmentId(Long shipmentId);
    long countByShipmentIdAndType(Long shipmentId, ShipmentException.ExceptionType type);
}