package com.example.admin_service.kafka;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@RequiredArgsConstructor
public class KafkaAdminNotificationSender {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaAdminNotificationSender.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String msg) {
        kafkaTemplate.send(KafkaConstant.ADMIN_NOTIFICATION_TOPIC, msg).whenComplete((result, ex) -> {
            if (ex != null) {
                LOG.error("Failed to publish Admin notification event: {}", msg, ex);
                return;
            }
            LOG.info("Published Admin notification event for Message: {}", msg);
        });
    }
}
