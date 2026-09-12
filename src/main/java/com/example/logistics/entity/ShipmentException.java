package com.example.logistics.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "exceptions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ShipmentException {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private User assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExceptionType type;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExceptionStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    @OneToMany(mappedBy = "exception", cascade = CascadeType.ALL)
    private List<ExceptionHistory> history;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = ExceptionStatus.OPEN;
    }

    public enum ExceptionType {
        DELAYED, DAMAGED, LOST, ADDRESS_ISSUE, FAILED_DELIVERY, PAYMENT_ISSUE
    }

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public enum ExceptionStatus {
        OPEN, ASSIGNED, INVESTIGATING, RESOLVED, CLOSED
    }
}