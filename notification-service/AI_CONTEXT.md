# Notification Service AI Context

This file gives AI agents and developers the local context for the notification service only.

## Maintenance Rule

When any AI agent or developer changes behavior, APIs, configuration, database schema, Kafka topics, message payloads, or business flow inside this notification service, update this file in the same change.

Keep this document focused on notification-service only. Cross-service behavior may be mentioned only where notification-service depends on it.

## Service Overview

- Service name: notification-service
- Runtime: Spring Boot with Java 17
- Main app class: `com.example.notification_service.NotificationServiceApplication`
- HTTP port: `8082`
- Database: MySQL database `notification_service`
- Kafka role: Consumer for inventory notification events, admin notification messages, and failed-service log events

## Main Responsibilities

- Consume inventory reservation success/failure notification events from Kafka.
- Consume raw admin notification messages from Kafka.
- Route inventory notification events by notification type.
- Send admin notification messages to both EMAIL and SMS handlers through separate consumer groups.
- Format and handle email notification events with a dummy logger implementation.
- Format and handle SMS notification events with a dummy logger implementation.
- Persist every supported success/failure inventory notification event in `notification_records`.
- Consume failed-service log events and persist them in `failed_svc_log`.

## Important Source Areas

- `src/main/java/com/example/notification_service/dto/FailedSvcLogEvent.java`: Kafka event DTO consumed for failed-service logs.
- `src/main/java/com/example/notification_service/dto/InventoryNotificationEvent.java`: Kafka event DTO consumed from inventory-service.
- `src/main/java/com/example/notification_service/dto/InventoryReservationStatus.java`: reservation result enum with `SUCCESS` and `FAILURE`.
- `src/main/java/com/example/notification_service/dto/NotificationType.java`: notification type enum with `EMAIL` and `SMS`.
- `src/main/java/com/example/notification_service/kafka/AdminNotificationEventListener.java`: Kafka listener for raw admin-service messages.
- `src/main/java/com/example/notification_service/kafka/FailedSvcLogEventListener.java`: Kafka listener for failed-service log events.
- `src/main/java/com/example/notification_service/kafka/KafkaConsumerConfig.java`: Kafka consumer factory configuration.
- `src/main/java/com/example/notification_service/kafka/InventoryNotificationEventListener.java`: Kafka topic listener and event handler.
- `src/main/java/com/example/notification_service/kafka/NotificationTopicConstant.java`: notification topic and group constants.
- `src/main/java/com/example/notification_service/model/FailedServiceLog.java`: JPA entity for persisted failed-service logs.
- `src/main/java/com/example/notification_service/model/NotificationRecord.java`: JPA entity for persisted notification history.
- `src/main/java/com/example/notification_service/repository/FailedServiceLogRepository.java`: Spring Data JPA repository for failed-service logs.
- `src/main/java/com/example/notification_service/repository/NotificationRepository.java`: Spring Data JPA repository for notification records.
- `src/main/java/com/example/notification_service/service/FailedServiceLogService.java`: failed-service log persistence and logging.
- `src/main/java/com/example/notification_service/service/NotificationService.java`: notification routing, persistence, and email/SMS handling.
- `src/main/resources/db/migration/V1__create_notification_records_table.sql`: Flyway migration for notification history table.
- `src/main/resources/db/migration/V2__create_failed_svc_log_table.sql`: Flyway migration for failed-service log table.
- `src/main/resources/application.properties`: local service, database, Flyway, and Kafka configuration.
- `src/main/resources/application-template.properties`: git-safe template configuration.

## Kafka Context

Kafka bootstrap server:

- `spring.kafka.bootstrap-servers=localhost:9092`

Consumed topics:

- `inventory_notification`
- `admin_notification`
- `failed_svc_log`

Consumer groups:

- `notification-service` for inventory notification events.
- `email_group` for admin EMAIL handling.
- `sms_group` for admin SMS handling.
- `all-service` for failed-service log events.

Consumed event payload:

```json
{
  "productName": "example-product",
  "requestedQuantity": 1,
  "reservationStatus": "SUCCESS",
  "notificationType": null,
  "message": "Inventory reserved successfully for product example-product"
}
```

Explicit `EMAIL` or `SMS` values route to one channel. A missing or null `notificationType` is the default behavior and routes to both channels.

Event DTO fields:

- `InventoryNotificationEvent.productName`
- `InventoryNotificationEvent.requestedQuantity`
- `InventoryNotificationEvent.reservationStatus`
- `InventoryNotificationEvent.notificationType`
- `InventoryNotificationEvent.message`

Consumer flow:

1. Inventory service publishes reservation success/failure events to topic `inventory_notification`.
2. `InventoryNotificationEventListener.listenToInventoryNotificationEvent()` receives the Kafka message as a string.
3. The listener logs the raw message.
4. The listener deserializes the message into `InventoryNotificationEvent` using `ObjectMapper`.
5. Invalid JSON payloads are logged and skipped.
6. `NotificationService.sendNotification()` routes by notification type.
7. Missing or null notification types are treated as the default and produce both EMAIL and SMS notifications.
8. `EMAIL` notifications are formatted by `createEmailBody()`, saved in `notification_records`, handled by `sendEmailNotification()`, and logged.
9. `SMS` notifications are formatted by `createSmsBody()`, saved in `notification_records`, handled by `sendSmsNotification()`, and logged.
10. Unsupported notification types are skipped and logged.

Admin notification flow:

1. Admin service publishes raw text messages to topic `admin_notification`.
2. `AdminNotificationEventListener.consumeAdminNotificationForEmailGroup()` receives each message as part of the EMAIL consumer group and calls `NotificationService.sendAdminEmailNotification()`.
3. `AdminNotificationEventListener.consumeAdminNotificationForSmsGroup()` receives each message as part of the SMS consumer group and calls `NotificationService.sendAdminSmsNotification()`.
4. Because admin messages are raw strings, notification-service cannot choose only EMAIL or only SMS from the payload today.
5. If admin-service needs one-channel routing, change the admin Kafka payload to JSON with a notification type field, or split admin notifications into separate email and SMS topics.

Failed-service log flow:

1. Any service can publish failed-service log JSON to topic `failed_svc_log`.
2. `FailedSvcLogEventListener.consumeFailedServiceLogEvent()` receives the deserialized `FailedSvcLogEvent` and logs service name, failed time, and reason length.
3. Kafka deserializes the JSON message into `FailedSvcLogEvent` through the failed-service-log listener container factory.
4. Processing failures are logged with service name and failed time, then rethrown so the listener container can handle the failure.
5. `FailedServiceLogService.saveFailedServiceLog()` logs the save attempt and persists the row.
6. Successful persistence logs the saved failed-service log id, service name, and failed time.

Failed-service log payload:

```json
{
  "failedReason": "Database connection failed",
  "failedTime": "2026-06-24T13:30:00",
  "svcName": "order-service"
}
```

## Database Context

Database name:

- `notification_service`

Flyway history table:

- `flyway_notification_history`

Flyway migrations:

- `V1__create_notification_records_table.sql`: creates `notification_records`.
- `V2__create_failed_svc_log_table.sql`: creates `failed_svc_log`.

Notification table:

- `notification_records`

Notification table columns:

- `id`: generated primary key.
- `product_name`: product from the inventory notification event.
- `requested_quantity`: quantity requested by the order.
- `reservation_status`: `SUCCESS` or `FAILURE`.
- `notification_type`: `EMAIL` or `SMS`.
- `message`: original message from inventory-service.
- `notification_body`: formatted email/SMS body generated by notification-service.
- `created_at`: persistence timestamp set before insert.

Failed-service log table:

- `failed_svc_log`

Failed-service log table columns:

- `id`: generated primary key.
- `svc_name`: service where the failure happened.
- `failed_reason`: failure reason sent by the source service.
- `failed_time`: time sent by the source service and stored as a database date-time value.
- `created_at`: persistence timestamp set by Hibernate.

## Current Behavior Notes

- The service persists supported inventory `EMAIL` and `SMS` notification records before logging the dummy send action.
- A missing or null inventory `notificationType` creates two persisted rows: one `EMAIL` row and one `SMS` row.
- Admin notifications are logged through dummy EMAIL and SMS handlers and are not persisted in `notification_records` currently.
- Failed-service log events are persisted in `failed_svc_log` and include structured logs for receive, save, success, and failure paths.
- The service does not currently call external email or SMS providers.
- `EMAIL` and `SMS` notification types both have dummy formatting and logging handlers.
- If changing the topic name, payload fields, consumer group, serialization, table schema, or persistence behavior, update this file and coordinate with inventory-service/admin-service where needed.

## Known Risks To Consider Before Changes

- Email and SMS delivery are only simulated with logging; add provider integrations before production use.
- There is no dead-letter topic or retry policy for invalid payloads or notification delivery failures.
- The event DTO is duplicated with inventory-service; keep both copies synchronized unless a shared contract module is introduced.
- Notification persistence depends on the local MySQL `notification_service` database being available and migrated by Flyway.
