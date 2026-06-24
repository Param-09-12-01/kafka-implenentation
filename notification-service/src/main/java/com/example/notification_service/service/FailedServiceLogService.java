package com.example.notification_service.service;

import com.example.notification_service.dto.FailedSvcLogEvent;
import com.example.notification_service.model.FailedServiceLog;
import com.example.notification_service.repository.FailedServiceLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FailedServiceLogService {

    private static final Logger LOG = LoggerFactory.getLogger(FailedServiceLogService.class);

    private final FailedServiceLogRepository failedServiceLogRepository;

    public void saveFailedServiceLog(FailedSvcLogEvent event) {
        LOG.info("Saving failed service log. svcName: {}, failedTime: {}, reasonLength: {}",
                event.getSvcName(), event.getFailedTime(), getReasonLength(event));

        FailedServiceLog failedServiceLog = FailedServiceLog.builder()
                .failedReason(event.getFailedReason())
                .svcName(event.getSvcName())
                .failedTime(event.getFailedTime())
                .build();

        FailedServiceLog savedLog = failedServiceLogRepository.save(failedServiceLog);
        LOG.info("Saved failed service log. id: {}, svcName: {}, failedTime: {}",
                savedLog.getId(), savedLog.getSvcName(), savedLog.getFailedTime());
    }

    private int getReasonLength(FailedSvcLogEvent event) {
        return event.getFailedReason() == null ? 0 : event.getFailedReason().length();
    }
}
