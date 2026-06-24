package com.example.notification_service.kafka;

import com.example.notification_service.dto.InventoryNotificationEvent;
import com.example.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class InventoryNotificationEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryNotificationEventListener.class);

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @org.springframework.kafka.annotation.KafkaListener(topics = NotificationTopicConstant.INVENTORY_NOTIFICATION, groupId = "notification-service")
    public void listenToInventoryNotificationEvent(String message) {
        LOG.info("Received inventory notification event: {}", message);
        try {
            InventoryNotificationEvent event = objectMapper.readValue(message, InventoryNotificationEvent.class);
            notificationService.sendNotification(event);
        } catch (JacksonException ex) {
            LOG.error("Failed to deserialize inventory notification event payload: {}", message, ex);
        }
    }
}
