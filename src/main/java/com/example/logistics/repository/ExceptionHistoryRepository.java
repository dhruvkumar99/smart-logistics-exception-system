package com.example.logistics.repository;

import com.example.logistics.entity.ExceptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExceptionHistoryRepository extends JpaRepository<ExceptionHistory, Long> {
    List<ExceptionHistory> findByExceptionIdOrderByChangedAtDesc(Long exceptionId);
}