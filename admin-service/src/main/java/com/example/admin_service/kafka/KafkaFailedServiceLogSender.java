package com.example.admin_service.kafka;

import com.example.admin_service.dto.FailedSvcLogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaFailedServiceLogSender {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaFailedServiceLogSender.class);

    private final KafkaTemplate<String, FailedSvcLogEvent> failedServiceLogKafkaTemplate;

    public KafkaFailedServiceLogSender(
            @Qualifier(KafkaProducerConfig.FAILED_SERVICE_LOG_KAFKA_TEMPLATE)
            KafkaTemplate<String, FailedSvcLogEvent> failedServiceLogKafkaTemplate) {
        this.failedServiceLogKafkaTemplate = failedServiceLogKafkaTemplate;
    }

    public void publishFailedServiceLogEvent(FailedSvcLogEvent event) {
        LOG.info("Publishing failed service log event to Kafka. topic: {}, svcName: {}, failedTime: {}",
                KafkaConstant.FAILED_SERVICE_LOG_TOPIC, event.getSvcName(), event.getFailedTime());
        failedServiceLogKafkaTemplate.send(KafkaConstant.FAILED_SERVICE_LOG_TOPIC, event).whenComplete((result, ex) -> {
            if (ex != null) {
                LOG.error("Failed to publish failed service log event. topic: {}, svcName: {}, failedTime: {}",
                        KafkaConstant.FAILED_SERVICE_LOG_TOPIC, event.getSvcName(), event.getFailedTime(), ex);
                return;
            }
            LOG.info("Published failed service log event. topic: {}, partition: {}, offset: {}, svcName: {}",
                    result.getRecordMetadata().topic(), result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset(), event.getSvcName());
        });
    }
}
