package com.example.logistics.dto;

import com.example.logistics.entity.Shipment.ShipmentStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ShipmentResponse {
    private Long id;
    private String trackingNumber;
    private String orderId;
    private String customerName;
    private String origin;
    private String destination;
    private ShipmentStatus status;
    private LocalDate expectedDeliveryDate;
    private LocalDate actualDeliveryDate;
    private LocalDateTime createdAt;
}