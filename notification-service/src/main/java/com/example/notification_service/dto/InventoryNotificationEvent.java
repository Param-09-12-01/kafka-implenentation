package com.example.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryNotificationEvent {
    private String productName;
    private Long requestedQuantity;
    private InventoryReservationStatus reservationStatus;
    private NotificationType notificationType;
    private String message;
}
