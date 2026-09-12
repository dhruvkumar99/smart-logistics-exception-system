package com.example.logistics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ShipmentRequest {

    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Origin is required")
    private String origin;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Expected delivery date is required")
    private LocalDate expectedDeliveryDate;
}