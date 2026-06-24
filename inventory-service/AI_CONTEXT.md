# Inventory Service AI Context

This file gives AI agents and developers the local context for the inventory service only.

## Maintenance Rule

When any AI agent or developer changes behavior, APIs, configuration, database schema, Kafka topics, message payloads, or business flow inside this inventory service, update this file in the same change.

Keep this document focused on inventory-service only. Cross-service behavior may be mentioned only where inventory-service depends on it.

## Service Overview

- Service name: inventory-service
- Runtime: Spring Boot with Java 17
- Main app class: `com.example.inventory_service.InventoryServiceApplication`
- HTTP port: `8080`
- Database: MySQL database `inventory_service`
- Persistence: Spring Data JPA with Flyway migrations
- Kafka role: Consumer for order-created inventory events and producer for inventory notification and failed-service log events

## Main Responsibilities

- Manage inventory records through REST endpoints.
- Persist inventory records in the inventory service database.
- Consume order-created events from Kafka.
- Deduct stock when enough inventory is available for an ordered product.
- Publish inventory reservation success/failure notification events to Kafka.
- Publish failed-service log events to Kafka when REST exceptions are handled.

## Important Source Areas

- `src/main/java/com/example/inventory_service/controller/InventoryController.java`: inventory REST API.
- `src/main/java/com/example/inventory_service/exception/ApiError.java`: common API error response body.
- `src/main/java/com/example/inventory_service/exception/GlobalExceptionHandler.java`: centralized REST exception handling for this service.
- `src/main/java/com/example/inventory_service/service/InventoryService.java`: inventory business logic and stock reservation.
- `src/main/java/com/example/inventory_service/model/Inventory.java`: inventory JPA entity.
- `src/main/java/com/example/inventory_service/repository/InventoryRepository.java`: inventory database access and stock-check query.
- `src/main/java/com/example/inventory_service/dto/InventoryEvent.java`: Kafka event DTO consumed from order-service.
- `src/main/java/com/example/inventory_service/dto/InventoryNotificationEvent.java`: Kafka event DTO published to notification-service.
- `src/main/java/com/example/inventory_service/dto/InventoryReservationStatus.java`: reservation result enum with `SUCCESS` and `FAILURE`.
- `src/main/java/com/example/inventory_service/dto/NotificationType.java`: notification type enum with `EMAIL` and `SMS`.
- `src/main/java/com/example/inventory_service/kafka/KafkaConsumerConfig.java`: Kafka consumer factory configuration.
- `src/main/java/com/example/inventory_service/kafka/KafkaProducerConfig.java`: Kafka producer configuration for notification and failed-service log events.
- `src/main/java/com/example/inventory_service/kafka/InventoryOrderEventListener.java`: order-created Kafka listener and reservation handler.
- `src/main/java/com/example/inventory_service/kafka/InventoryNotificationEventPublisher.java`: publisher for reservation notification events.
- `src/main/java/com/example/inventory_service/kafka/KafkaFailedServiceLogSender.java`: publisher for failed-service log events.
- `src/main/java/com/example/inventory_service/kafka/NotificationTopicConstant.java`: Kafka topic constants for order-created, notification, and failed-service log topics.
- `src/main/resources/application.properties`: service, database, Flyway, and Kafka configuration.
- `src/main/resources/db/migration`: Flyway migrations for inventory-service database schema.

## REST API

Base path: `/api/inventory`

- `POST /api/inventory`: saves an inventory record.

Current controller does not expose a `GET /api/inventory/{productName}` endpoint.

## Data Model

`Inventory`

- `id`: generated primary key.
- `productName`: product name.
- `quantity`: available quantity.
- `price`: product price.

Database constraint:

- `product_name` is unique, so one inventory row is expected per product name.

## Database And Flyway

Configured in `src/main/resources/application.properties`:

- `spring.datasource.url=jdbc:mysql://localhost:3306/inventory_service`
- `spring.datasource.username=root`
- `spring.datasource.password=root`
- `spring.jpa.hibernate.ddl-auto=none`
- `spring.flyway.enabled=true`
- `spring.flyway.locations=classpath:db/migration`
- `spring.flyway.table=flyway_inventory_history`

Current migrations:

- `V1__create_inventory_table.sql`: creates `inventory` table with `id`, `product_name`, `quantity`, and `price`.
- `V2__add_unique_constraint.sql`: adds unique constraint on `product_name`.

## Kafka Context

Kafka bootstrap server:

- `spring.kafka.bootstrap-servers=localhost:9092`

Consumed topic:

- `inventory_order_created`

Consumer group:

- `inventory-service`

Consumed event payload:

```json
{
  "productName": "example-product",
  "quantity": 1
}
```

Published topics:

- `inventory_notification`
- `failed_svc_log`

Published event payload:

```json
{
  "productName": "example-product",
  "requestedQuantity": 1,
  "reservationStatus": "SUCCESS",
  "notificationType": null,
  "message": "Inventory reserved successfully for product example-product"
}
```

`notificationType` is intentionally left unset by the default inventory flow. Notification-service treats a missing or null value as the default multi-channel notification and sends both `EMAIL` and `SMS`.

Consumer and producer flow:

1. Order service publishes an order-created inventory event to topic `inventory_order_created`.
2. `InventoryOrderEventListener.listenToOrderCreatedEvent()` receives the Kafka message as a string.
3. The listener logs the raw message.
4. The listener deserializes the message into `InventoryEvent` using `ObjectMapper`.
5. Invalid JSON payloads are logged and skipped.
6. `handleOrderCreatedEvent()` calls `InventoryService.reserveStock()`.
7. `InventoryService.reserveStock()` finds inventory by product name and decrements quantity when enough stock exists.
8. The listener builds an `InventoryNotificationEvent` with `SUCCESS` or `FAILURE`, no explicit notification type, and a human-readable message.
9. `InventoryNotificationEventPublisher.publish()` sends the event to topic `inventory_notification`.

## Stock Check Behavior

Current stock-check query:

```java
select case when count(i) > 0 then true else false end from Inventory i where i.productName = :productName and i.quantity >= :orderedQuantity
```

Current behavior:

- Checks whether available quantity is greater than or equal to ordered quantity.
- Reduces inventory quantity when `reserveStock()` succeeds.
- Publishes a success or failure notification event after handling each valid order-created event.
- Does not currently notify order-service whether stock is available.

## Exception Handling

- REST exceptions are handled in one place by `GlobalExceptionHandler`.
- `ApiError` is the common error response shape with timestamp, HTTP status, error, message, and path.
- `IllegalArgumentException` returns `400 BAD_REQUEST`.
- `NoSuchElementException` returns `404 NOT_FOUND`.
- `DataIntegrityViolationException` returns `409 CONFLICT`.
- Any other unhandled exception returns `500 INTERNAL_SERVER_ERROR` and is logged.
- Handled REST exceptions publish a `FailedSvcLogEvent` to topic `failed_svc_log` with `svcName=inventory-service`, the failure reason, and the failure time.

## Current Behavior Notes

- Inventory records can be created with `POST /api/inventory`.
- Inventory save, delete, stock reservation, and handled exception paths include structured logs.
- Kafka consumption, notification publishing, and failed-service log publishing are asynchronous.
- Default notification events omit `notificationType`, so notification-service sends both email and SMS.
- If changing topic names, payload fields, consumer group, or serialization, update this file and coordinate with order-service and notification-service.

## Known Risks To Consider Before Changes

- Inventory reservation is not concurrency-safe for high parallel order volume; consider row locking or conditional update queries before production use.
- Inventory service publishes notification events, but still does not publish a result event back to order-service.
- Order-service may still persist orders even when inventory reservation fails asynchronously.
- The notification event DTO is duplicated with notification-service; keep both copies synchronized unless a shared contract module is introduced.
