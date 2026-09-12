package com.example.logistics.dto;

import com.example.logistics.entity.ShipmentException.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExceptionResponse {
    private Long id;
    private Long shipmentId;
    private String trackingNumber;
    private ExceptionType type;
    private String description;
    private Priority priority;
    private ExceptionStatus status;
    private Long assignedToId;
    private String assignedToName;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}