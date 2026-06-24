package com.example.notification_service.kafka;

import com.example.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminNotificationEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(AdminNotificationEventListener.class);

    private final NotificationService notificationService;

    /**
     * Admin-service currently publishes a raw string message to one topic. Because there is no
     * notification type in that payload, this service uses two consumer groups on the same topic
     * so every admin message is handled once for EMAIL and once for SMS. If admin-service needs to
     * send only one channel, change the Kafka message to JSON and add a notification type field, or
     * split admin notifications into separate email and SMS topics.
     */
    @KafkaListener(topics = NotificationTopicConstant.ADMIN_SVC_NOTIFICATION, groupId = NotificationTopicConstant.ADMIN_SVC_NOTIFICATION_EMAIL_GROUP_ID)
    public void consumeAdminNotificationForEmailGroup(String message) {
        LOG.info("Received admin notification for EMAIL group. messageLength: {}", message.length());
        notificationService.sendAdminEmailNotification(message);
    }

    @KafkaListener(topics = NotificationTopicConstant.ADMIN_SVC_NOTIFICATION, groupId = NotificationTopicConstant.ADMIN_SVC_NOTIFICATION_SMS_GROUP_ID)
    public void consumeAdminNotificationForSmsGroup(String message) {
        LOG.info("Received admin notification for SMS group. messageLength: {}", message.length());
        notificationService.sendAdminSmsNotification(message);
    }
}
