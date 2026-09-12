package com.example.logistics.controller;

import com.example.logistics.dto.ExceptionRequest;
import com.example.logistics.dto.ExceptionResponse;
import com.example.logistics.entity.ExceptionHistory;
import com.example.logistics.entity.ShipmentException.ExceptionStatus;
import com.example.logistics.entity.ShipmentException.ExceptionType;
import com.example.logistics.entity.ShipmentException.Priority;
import com.example.logistics.service.ExceptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exceptions")
@RequiredArgsConstructor
public class ExceptionController {

    private final ExceptionService exceptionService;

    @PostMapping
    public ResponseEntity<ExceptionResponse> create(@Valid @RequestBody ExceptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exceptionService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ExceptionResponse>> getAll() {
        return ResponseEntity.ok(exceptionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExceptionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(exceptionService.getById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ExceptionResponse>> getByStatus(@PathVariable ExceptionStatus status) {
        return ResponseEntity.ok(exceptionService.getByStatus(status));
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<ExceptionResponse>> getByPriority(@PathVariable Priority priority) {
        return ResponseEntity.ok(exceptionService.getByPriority(priority));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ExceptionResponse>> getByType(@PathVariable ExceptionType type) {
        return ResponseEntity.ok(exceptionService.getByType(type));
    }

    @GetMapping("/assigned/{userId}")
    public ResponseEntity<List<ExceptionResponse>> getByAssignedUser(@PathVariable Long userId) {
        return ResponseEntity.ok(exceptionService.getByAssignedUser(userId));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ExceptionHistory>> getHistory(@PathVariable Long id) {
        return ResponseEntity.ok(exceptionService.getHistory(id));
    }

    @PutMapping("/{id}/assign/{userId}")
    public ResponseEntity<ExceptionResponse> assign(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(exceptionService.assign(id, userId));
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<ExceptionResponse> resolve(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "SYSTEM") String changedBy,
            @RequestParam(required = false) String remarks) {
        return ResponseEntity.ok(exceptionService.resolve(id, changedBy, remarks));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ExceptionResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam ExceptionStatus status,
            @RequestParam(required = false, defaultValue = "SYSTEM") String changedBy,
            @RequestParam(required = false) String remarks) {
        return ResponseEntity.ok(exceptionService.updateStatus(id, status, changedBy, remarks));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        exceptionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}