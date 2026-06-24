# Kafka Implementation Microservices

This repository contains four Spring Boot microservices that communicate with Kafka and use MySQL for persistence where needed.

## Services

| Service | Port | Database | Main responsibility |
| --- | --- | --- | --- |
| order-service | 8081 | order_service | Creates orders and publishes order-created inventory events. |
| inventory-service | 8080 | inventory_service | Stores stock, consumes order-created events, reserves inventory, and publishes notification events. |
| notification-service | 8082 | notification_service | Consumes inventory/admin notification events, formats email/SMS notifications, and stores inventory notification history. |
| admin-service | 8083 | none | Exposes admin notification API and publishes admin messages to Kafka. |

No port collision is present in the current service configuration.

## Kafka Topics

| Topic | Producer | Consumer | Payload |
| --- | --- | --- | --- |
| inventory_order_created | order-service | inventory-service | Product name and requested quantity. |
| inventory_notification | inventory-service | notification-service | Product, quantity, reservation status, optional notification type, and message. |
| admin_notification | admin-service | notification-service | Raw admin notification text. |

## Local Setup

1. Start MySQL and Kafka locally.
2. Create these MySQL databases:
   - order_service
   - inventory_service
   - notification_service
3. Copy each service template config to a local config file:
   - order-service/src/main/resources/application-template.properties -> order-service/src/main/resources/application.properties
   - inventory-service/src/main/resources/application-template.properties -> inventory-service/src/main/resources/application.properties
   - notification-service/src/main/resources/application-template.properties -> notification-service/src/main/resources/application.properties
   - admin-service/src/main/resources/application-template.properties -> admin-service/src/main/resources/application.properties
4. Update local database usernames/passwords in each database-backed application.properties file.
5. Run each service from its own folder with mvnw.cmd spring-boot:run.

## Admin Notification API

Admin-service exposes:

```http
POST /api/admin/notifications
```

Request body is raw text. Admin-service publishes that text to the `admin_notification` Kafka topic. Notification-service currently consumes the same admin topic with separate EMAIL and SMS consumer groups, so each admin message is handled for both channels.

## Git Safety

application.properties files are ignored by git because they can contain local credentials. Commit application-template.properties files instead.

## Validation

Run tests from each service directory:

```bash
mvnw.cmd clean test
```

## AI Context Files

Each service has an AI_CONTEXT.md file. If behavior, configuration, database schema, Kafka topics, payloads, or business flow changes inside a service, update that service's AI_CONTEXT.md in the same change.
