package com.example.notification_service.kafka;

import com.example.notification_service.dto.FailedSvcLogEvent;
import com.example.notification_service.service.FailedServiceLogService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FailedSvcLogEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(FailedSvcLogEventListener.class);

    private final FailedServiceLogService failedServiceLogService;

    @KafkaListener(topics = NotificationTopicConstant.FAILED_SERVICE_LOG_TOPIC,
            groupId = NotificationTopicConstant.FAILED_SERVICE_LOG_GROUP_ID,
            containerFactory = "failedServiceLogListenerContainerFactory")
    public void consumeFailedServiceLogEvent(FailedSvcLogEvent event) {
        LOG.info("Received failed service log event. svcName: {}, failedTime: {}, reasonLength: {}",
                event.getSvcName(), event.getFailedTime(), getReasonLength(event));
        try {
            failedServiceLogService.saveFailedServiceLog(event);
            LOG.info("Processed failed service log event. svcName: {}, failedTime: {}",
                    event.getSvcName(), event.getFailedTime());
        } catch (Exception ex) {
            LOG.error("Failed to process failed service log event. svcName: {}, failedTime: {}",
                    event.getSvcName(), event.getFailedTime(), ex);
            throw ex;
        }
    }

    private int getReasonLength(FailedSvcLogEvent event) {
        return event.getFailedReason() == null ? 0 : event.getFailedReason().length();
    }
}
