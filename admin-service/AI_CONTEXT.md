# Admin Service AI Context

This file gives AI agents and developers the local context for the admin service only.

## Maintenance Rule

When any AI agent or developer changes behavior, APIs, configuration, Kafka topics, message payloads, or business flow inside this admin service, update this file in the same change.

Keep this document focused on admin-service only. Cross-service behavior may be mentioned only where admin-service depends on it.

## Service Overview

- Service name: admin-service
- Runtime: Spring Boot with Java 17
- Main app class: `com.example.admin_service.AdminServiceApplication`
- HTTP port: `8083`
- Database: none currently
- Kafka role: Producer for admin notification messages

## Main Responsibilities

- Expose an admin HTTP endpoint for publishing notification messages.
- Validate that admin notification messages are not blank.
- Publish validated admin notification messages to Kafka.
- Keep Kafka producer behavior in the existing kafka package unless a change is explicitly required.

## Important Source Areas

- `src/main/java/com/example/admin_service/controller/AdminController.java`: REST endpoint for admin notification requests.
- `src/main/java/com/example/admin_service/service/AdminNotificationService.java`: request validation, logging, and Kafka publish call.
- `src/main/java/com/example/admin_service/kafka/KafkaAdminNotificationSender.java`: Kafka sender for admin notifications.
- `src/main/java/com/example/admin_service/kafka/KafkaConstant.java`: admin topic constant.
- `src/main/java/com/example/admin_service/kafka/KafkaProducerConfig.java`: Kafka producer configuration.
- `src/main/java/com/example/admin_service/kafka/KafkaTopic.java`: Kafka topic declaration.
- `src/main/resources/application.properties`: local runtime configuration, ignored by git.
- `src/main/resources/application-template.properties`: git-safe template configuration.

## API Context

Endpoint:

- `POST /api/admin/notifications`

Request body:

- Raw text message.

Behavior:

1. `AdminController.sendAdminNotification()` receives the request body.
2. `AdminNotificationService.sendAdminNotification()` rejects blank messages.
3. Valid messages are trimmed and sent through `KafkaAdminNotificationSender`.
4. The service returns `Admin notification published successfully` after the publish request is accepted.

## Kafka Context

Kafka bootstrap server:

- `spring.kafka.bootstrap-servers=localhost:9092`

Produced topic:

- `admin_notification`

Payload:

- Raw string message.

Downstream behavior:

- notification-service consumes the same admin topic with separate EMAIL and SMS consumer groups.
- Because the payload is a raw string, it does not currently carry a channel/type field.
- If admin notifications need to target only EMAIL or only SMS, change the Kafka payload to JSON with a notification type field, or split admin notifications into separate email and SMS topics.

## Current Behavior Notes

- The service logs rejected blank messages and accepted publish requests.
- There is no admin database or persistence in this service.
- The API accepts a plain text body, not a JSON request object.
- If changing the endpoint, topic name, payload format, or Kafka producer behavior, update this file and coordinate with notification-service where needed.

## Known Risks To Consider Before Changes

- Admin notification delivery is asynchronous after the Kafka send request is submitted.
- The raw string payload is simple but cannot express per-channel routing or metadata.
- There is no authentication or authorization on the admin endpoint yet.
