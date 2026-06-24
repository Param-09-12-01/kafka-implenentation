package com.example.inventory_service.kafka;

import com.example.inventory_service.dto.InventoryEvent;
import com.example.inventory_service.dto.InventoryNotificationEvent;
import com.example.inventory_service.dto.InventoryReservationStatus;
import com.example.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class InventoryOrderEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryOrderEventListener.class);

    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;
    private final InventoryNotificationEventPublisher inventoryNotificationEventPublisher;

    @KafkaListener(topics = NotificationTopicConstant.ORDER_CREATED, groupId = "inventory-service")
    public void listenToOrderCreatedEvent(String message) {
        LOG.info("Received order-created inventory event: {}", message);
        try {
            InventoryEvent event = objectMapper.readValue(message, InventoryEvent.class);
            handleOrderCreatedEvent(event);
        } catch (JacksonException ex) {
            LOG.error("Failed to deserialize order-created inventory event payload: {}", message, ex);
        }
    }

    private void handleOrderCreatedEvent(InventoryEvent event) {
        boolean stockReserved = inventoryService.reserveStock(event.getProductName(), event.getQuantity());
        InventoryNotificationEvent notificationEvent = createNotificationEvent(event, stockReserved);

        LOG.info(
                "Inventory reservation completed. productName: {}, requestedQuantity: {}, reserved: {}",
                event.getProductName(),
                event.getQuantity(),
                stockReserved
        );
        inventoryNotificationEventPublisher.publish(notificationEvent);
    }

    private InventoryNotificationEvent createNotificationEvent(InventoryEvent event, boolean stockReserved) {
        InventoryReservationStatus status = stockReserved
                ? InventoryReservationStatus.SUCCESS
                : InventoryReservationStatus.FAILURE;

        return InventoryNotificationEvent.builder()
                .productName(event.getProductName())
                .requestedQuantity(event.getQuantity())
                .reservationStatus(status)
                .message(createNotificationMessage(event, status))
                .build();
    }

    private String createNotificationMessage(InventoryEvent event, InventoryReservationStatus status) {
        if (InventoryReservationStatus.SUCCESS.equals(status)) {
            return "Inventory reserved successfully for product " + event.getProductName();
        }
        return "Inventory reservation failed for product " + event.getProductName();
    }
}
