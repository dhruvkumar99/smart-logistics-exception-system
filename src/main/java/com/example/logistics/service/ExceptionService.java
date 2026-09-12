package com.example.logistics.service;

import com.example.logistics.dto.ExceptionRequest;
import com.example.logistics.dto.ExceptionResponse;
import com.example.logistics.entity.*;
import com.example.logistics.entity.ShipmentException.*;
import com.example.logistics.exception.BusinessException;
import com.example.logistics.exception.ResourceNotFoundException;
import com.example.logistics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExceptionService {

    private final ExceptionRepository exceptionRepository;
    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;
    private final ExceptionHistoryRepository historyRepository;

    // ---------- CREATE ----------
    @Transactional
    public ExceptionResponse create(ExceptionRequest request) {
        Shipment shipment = shipmentRepository.findById(request.getShipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + request.getShipmentId()));

        Priority priority = calculatePriority(shipment.getId(), request.getType());

        ShipmentException exception = ShipmentException.builder()
                .shipment(shipment)
                .type(request.getType())
                .description(request.getDescription())
                .priority(priority)
                .status(ExceptionStatus.OPEN)
                .build();

        ShipmentException saved = exceptionRepository.save(exception);

        // Mark shipment as EXCEPTION
        shipment.setStatus(Shipment.ShipmentStatus.EXCEPTION);
        shipmentRepository.save(shipment);

        recordHistory(saved, null, ExceptionStatus.OPEN, "SYSTEM", "Exception created");

        return toResponse(saved);
    }

    // ---------- PRIORITY CALCULATION (business rule) ----------
    public Priority calculatePriority(Long shipmentId, ExceptionType type) {
        // Lost or damaged => HIGH
        if (type == ExceptionType.LOST || type == ExceptionType.DAMAGED) {
            return Priority.HIGH;
        }
        // 2+ failed attempts => HIGH
        long failedAttempts = exceptionRepository
                .countByShipmentIdAndType(shipmentId, ExceptionType.FAILED_DELIVERY);
        if (failedAttempts >= 2) {
            return Priority.HIGH;
        }
        // 1 failed attempt => MEDIUM
        if (type == ExceptionType.FAILED_DELIVERY) {
            return Priority.MEDIUM;
        }
        // Payment / address issues => MEDIUM
        if (type == ExceptionType.PAYMENT_ISSUE || type == ExceptionType.ADDRESS_ISSUE) {
            return Priority.MEDIUM;
        }
        // Minor delay => LOW
        return Priority.LOW;
    }

    // ---------- READ ----------
    public List<ExceptionResponse> getAll() {
        return exceptionRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ExceptionResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public List<ExceptionResponse> getByStatus(ExceptionStatus status) {
        return exceptionRepository.findByStatus(status).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ExceptionResponse> getByPriority(Priority priority) {
        return exceptionRepository.findByPriority(priority).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ExceptionResponse> getByType(ExceptionType type) {
        return exceptionRepository.findByType(type).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ExceptionResponse> getByAssignedUser(Long userId) {
        return exceptionRepository.findByAssignedToId(userId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ExceptionHistory> getHistory(Long exceptionId) {
        findEntity(exceptionId);
        return historyRepository.findByExceptionIdOrderByChangedAtDesc(exceptionId);
    }

    // ---------- ASSIGN ----------
    @Transactional
    public ExceptionResponse assign(Long exceptionId, Long userId) {
        ShipmentException ex = findEntity(exceptionId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (!user.isActive()) {
            throw new BusinessException("Cannot assign to inactive user: " + user.getName());
        }

        ex.setAssignedTo(user);
        ExceptionStatus old = ex.getStatus();
        ex.setStatus(ExceptionStatus.ASSIGNED);
        exceptionRepository.save(ex);

        recordHistory(ex, old, ExceptionStatus.ASSIGNED, "SYSTEM",
                "Assigned to " + user.getName());

        return toResponse(ex);
    }

    // ---------- UPDATE STATUS ----------
    @Transactional
    public ExceptionResponse updateStatus(Long id, ExceptionStatus newStatus, String changedBy, String remarks) {
        ShipmentException ex = findEntity(id);
        ExceptionStatus old = ex.getStatus();

        if (old == ExceptionStatus.CLOSED) {
            throw new BusinessException("Cannot change status of a CLOSED exception");
        }

        ex.setStatus(newStatus);
        if (newStatus == ExceptionStatus.RESOLVED || newStatus == ExceptionStatus.CLOSED) {
            ex.setResolvedAt(LocalDateTime.now());
        }
        exceptionRepository.save(ex);

        recordHistory(ex, old, newStatus, changedBy, remarks);
        return toResponse(ex);
    }

    // ---------- RESOLVE ----------
    @Transactional
    public ExceptionResponse resolve(Long id, String changedBy, String remarks) {
        ShipmentException ex = findEntity(id);
        ExceptionStatus old = ex.getStatus();

        if (old == ExceptionStatus.CLOSED) {
            throw new BusinessException("Exception is already CLOSED");
        }

        ex.setStatus(ExceptionStatus.RESOLVED);
        ex.setResolvedAt(LocalDateTime.now());
        exceptionRepository.save(ex);

        recordHistory(ex, old, ExceptionStatus.RESOLVED, changedBy, remarks);
        return toResponse(ex);
    }

    // ---------- DELETE ----------
    public void delete(Long id) {
        exceptionRepository.delete(findEntity(id));
    }

    // ---------- HELPERS ----------
    public ShipmentException findEntity(Long id) {
        return exceptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exception not found: " + id));
    }

    private void recordHistory(ShipmentException ex, ExceptionStatus oldStatus,
                               ExceptionStatus newStatus, String changedBy, String remarks) {
        ExceptionHistory history = ExceptionHistory.builder()
                .exception(ex)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .remarks(remarks)
                .build();
        historyRepository.save(history);
    }

    private ExceptionResponse toResponse(ShipmentException ex) {
        return ExceptionResponse.builder()
                .id(ex.getId())
                .shipmentId(ex.getShipment().getId())
                .trackingNumber(ex.getShipment().getTrackingNumber())
                .type(ex.getType())
                .description(ex.getDescription())
                .priority(ex.getPriority())
                .status(ex.getStatus())
                .assignedToId(ex.getAssignedTo() != null ? ex.getAssignedTo().getId() : null)
                .assignedToName(ex.getAssignedTo() != null ? ex.getAssignedTo().getName() : null)
                .createdAt(ex.getCreatedAt())
                .resolvedAt(ex.getResolvedAt())
                .build();
    }
}