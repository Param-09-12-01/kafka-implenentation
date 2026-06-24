package com.example.admin_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FailedSvcLogEvent {
    private String failedReason;
    private LocalDateTime failedTime;
    private String svcName;
}
