package com.example.notification_service.repository;

import com.example.notification_service.model.FailedServiceLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedServiceLogRepository extends JpaRepository<FailedServiceLog, Long> {
}