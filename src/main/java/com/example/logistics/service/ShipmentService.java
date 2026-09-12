package com.example.logistics.service;

import com.example.logistics.dto.ShipmentRequest;
import com.example.logistics.dto.ShipmentResponse;
import com.example.logistics.entity.Shipment;
import com.example.logistics.entity.Shipment.ShipmentStatus;
import com.example.logistics.exception.BusinessException;
import com.example.logistics.exception.ResourceNotFoundException;
import com.example.logistics.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    public ShipmentResponse create(ShipmentRequest request) {
        if (shipmentRepository.existsByTrackingNumber(request.getTrackingNumber())) {
            throw new BusinessException("Tracking number already exists: " + request.getTrackingNumber());
        }
        Shipment shipment = Shipment.builder()
                .trackingNumber(request.getTrackingNumber())
                .orderId(request.getOrderId())
                .customerName(request.getCustomerName())
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .status(ShipmentStatus.CREATED)
                .expectedDeliveryDate(request.getExpectedDeliveryDate())
                .build();
        return toResponse(shipmentRepository.save(shipment));
    }

    public List<ShipmentResponse> getAll() {
        return shipmentRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ShipmentResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public ShipmentResponse updateStatus(Long id, ShipmentStatus status) {
        Shipment shipment = findEntity(id);
        shipment.setStatus(status);
        if (status == ShipmentStatus.DELIVERED) {
            shipment.setActualDeliveryDate(LocalDate.now());
        }
        return toResponse(shipmentRepository.save(shipment));
    }

    public void delete(Long id) {
        shipmentRepository.delete(findEntity(id));
    }

    public Shipment findEntity(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + id));
    }

    private ShipmentResponse toResponse(Shipment s) {
        return ShipmentResponse.builder()
                .id(s.getId())
                .trackingNumber(s.getTrackingNumber())
                .orderId(s.getOrderId())
                .customerName(s.getCustomerName())
                .origin(s.getOrigin())
                .destination(s.getDestination())
                .status(s.getStatus())
                .expectedDeliveryDate(s.getExpectedDeliveryDate())
                .actualDeliveryDate(s.getActualDeliveryDate())
                .createdAt(s.getCreatedAt())
                .build();
    }
}