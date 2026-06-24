package com.example.notification_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "failed_svc_log")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FailedServiceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String svcName;
    private String failedReason;
    private LocalDateTime failedTime;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
