package com.example.notification_service.service;

import com.example.notification_service.dto.InventoryNotificationEvent;
import com.example.notification_service.dto.NotificationType;
import com.example.notification_service.model.NotificationRecord;
import com.example.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public void sendNotification(InventoryNotificationEvent event) {
        if (event.getNotificationType() == null) {
            sendEmailNotification(event);
            sendSmsNotification(event);
            return;
        }

        if (NotificationType.EMAIL.equals(event.getNotificationType())) {
            sendEmailNotification(event);
            return;
        }

        if (NotificationType.SMS.equals(event.getNotificationType())) {
            sendSmsNotification(event);
            return;
        }

        LOG.info("Skipping unsupported notification type: {} for productName: {}",
                event.getNotificationType(),
                event.getProductName());
    }

    private void saveNotificationRecord(InventoryNotificationEvent event, NotificationType notificationType, String notificationBody) {
        NotificationRecord notificationRecord = NotificationRecord.builder()
                .productName(event.getProductName())
                .requestedQuantity(event.getRequestedQuantity())
                .reservationStatus(event.getReservationStatus())
                .notificationType(notificationType)
                .message(event.getMessage())
                .notificationBody(notificationBody)
                .build();

        notificationRepository.save(notificationRecord);
    }

    private void sendEmailNotification(InventoryNotificationEvent event) {
        String notificationBody = createEmailBody(event);
        saveNotificationRecord(event, NotificationType.EMAIL, notificationBody);
        LOG.info("Sending email notification. body: {}", notificationBody);
    }

    private void sendSmsNotification(InventoryNotificationEvent event) {
        String notificationBody = createSmsBody(event);
        saveNotificationRecord(event, NotificationType.SMS, notificationBody);
        LOG.info("Sending SMS notification. body: {}", notificationBody);
    }

    private String createEmailBody(InventoryNotificationEvent event) {
        return "Inventory update: product=" + event.getProductName()
                + ", quantity=" + event.getRequestedQuantity()
                + ", status=" + event.getReservationStatus()
                + ", message=" + event.getMessage();
    }

    private String createSmsBody(InventoryNotificationEvent event) {
        return "Inventory " + event.getReservationStatus()
                + " for " + event.getProductName()
                + " (qty: " + event.getRequestedQuantity() + ")";
    }
}
