package com.example.logistics.dto;

import com.example.logistics.entity.ShipmentException.ExceptionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ExceptionRequest {

    @NotNull(message = "Shipment ID is required")
    private Long shipmentId;

    @NotNull(message = "Exception type is required")
    private ExceptionType type;

    @NotBlank(message = "Description is required")
    private String description;
}