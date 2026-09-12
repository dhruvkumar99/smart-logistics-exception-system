package com.example.logistics.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exception_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExceptionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exception_id", nullable = false)
    private ShipmentException exception;

    @Enumerated(EnumType.STRING)
    private ShipmentException.ExceptionStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentException.ExceptionStatus newStatus;

    private String changedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @Column(length = 500)
    private String remarks;

    @PrePersist
    public void prePersist() {
        this.changedAt = LocalDateTime.now();
    }
}