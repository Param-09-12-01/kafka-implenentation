package com.example.notification_service.kafka;

public final class NotificationTopicConstant {

    // Inventory SVC kafka constant
    public static final String INVENTORY_NOTIFICATION = "inventory_notification";
    public static final String INVENTORY_NOTIFICATION_TOPIC_GROUP_ID = "notification-service";


    // Admin notification SVC kafka constant
    public static final String ADMIN_SVC_NOTIFICATION = "admin_notification";
    public static final String ADMIN_SVC_NOTIFICATION_EMAIL_GROUP_ID = "email_group";
    public static final String ADMIN_SVC_NOTIFICATION_SMS_GROUP_ID = "sms_group";


    private NotificationTopicConstant() {
    }
}
