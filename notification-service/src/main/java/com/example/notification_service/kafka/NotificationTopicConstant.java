package com.example.notification_service.kafka;

public final class NotificationTopicConstant {

    public static final String INVENTORY_NOTIFICATION = "inventory_notification";
    public static final String INVENTORY_NOTIFICATION_TOPIC_GROUP_ID = "notification-service";

    public static final String ADMIN_SERVICE_NOTIFICATION = "admin_notification";
    public static final String ADMIN_SERVICE_NOTIFICATION_EMAIL_GROUP_ID = "email_group";
    public static final String ADMIN_SERVICE_NOTIFICATION_SMS_GROUP_ID = "sms_group";

    public static final String FAILED_SERVICE_LOG_TOPIC = "failed_svc_log";
    public static final String FAILED_SERVICE_LOG_GROUP_ID = "all-service";

    private NotificationTopicConstant() {
    }
}
