package com.example.inventory_service.kafka;

import com.example.inventory_service.dto.InventoryNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryNotificationEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryNotificationEventPublisher.class);

    private final KafkaTemplate<String, InventoryNotificationEvent> inventoryNotificationKafkaTemplate;

    public void publish(InventoryNotificationEvent event) {
        inventoryNotificationKafkaTemplate.send(NotificationTopicConstant.INVENTORY_NOTIFICATION, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        LOG.error("Failed to publish inventory notification event: {}", event, ex);
                        return;
                    }
                    LOG.info("Published inventory notification event for productName: {}, status: {}",
                            event.getProductName(),
                            event.getReservationStatus());
                });
    }
}
