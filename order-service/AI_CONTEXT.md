# Order Service AI Context

This file gives AI agents and developers the local context for the order service only.

## Maintenance Rule

When any AI agent or developer changes behavior, APIs, configuration, database schema, Kafka topics, message payloads, or business flow inside this order service, update this file in the same change.

Keep this document focused on order-service only. Cross-service behavior may be mentioned only where order-service depends on it.

## Service Overview

- Service name: order-service
- Runtime: Spring Boot with Java 17
- Main app class: `com.example.order_service.OrderServiceApplication`
- HTTP port: `8081`
- Database: MySQL database `order_service`
- Persistence: Spring Data JPA with Flyway migrations
- Kafka role: Producer for inventory-related order events

## Main Responsibilities

- Manage users through REST endpoints.
- Manage orders through REST endpoints.
- Persist orders and users in the order service database.
- Publish an inventory event to Kafka after an order is created.
- Provide a configured RestTemplate client for inventory-service, although the current order creation path publishes Kafka events instead of doing a synchronous stock check.

## Important Source Areas

- `src/main/java/com/example/order_service/controller/OrderController.java`: order REST API.
- `src/main/java/com/example/order_service/controller/UserController.java`: user REST API.
- `src/main/java/com/example/order_service/service/OrderService.java`: order business logic and Kafka event publishing.
- `src/main/java/com/example/order_service/service/UserService.java`: user business logic.
- `src/main/java/com/example/order_service/service/InventoryService.java`: REST client wrapper for inventory-service.
- `src/main/java/com/example/order_service/model/Order.java`: order JPA entity.
- `src/main/java/com/example/order_service/model/User.java`: user JPA entity.
- `src/main/java/com/example/order_service/kafka/KafkaConfig.java`: Kafka admin configuration.
- `src/main/java/com/example/order_service/kafka/KafkaProducerConfig.java`: Kafka producer and template config.
- `src/main/java/com/example/order_service/kafka/KafkaInventoryMessageSender.java`: sends inventory events.
- `src/main/java/com/example/order_service/kafka/KafkaInventoryTopic.java`: Kafka topic bean for order-created inventory events.
- `src/main/java/com/example/order_service/kafka/InventoryTopicConstant.java`: inventory topic constants.
- `src/main/resources/application.properties`: service, database, Flyway, Kafka, and inventory-service configuration.
- `src/main/resources/db/migration`: Flyway migrations for order-service database schema.

## REST API

Base path: `/api/orders`

- `GET /api/orders`: returns all orders.
- `GET /api/orders/{id}`: returns one order by id, or `null` if not found.
- `POST /api/orders`: creates an order, saves it, and publishes an inventory event.
- `DELETE /api/orders/{id}`: deletes an order by id.

Base path: `/api/users`

- `GET /api/users`: returns all users.
- `GET /api/users/{id}`: returns one user by id, or `null` if not found.
- `POST /api/users`: creates a user.
- `DELETE /api/users/{id}`: deletes a user by id.

## Data Model

`User`

- `id`: generated primary key.
- `name`: user name.
- `email`: unique email in the database.

`Order`

- `id`: generated primary key.
- `productName`: ordered product name.
- `amount`: order amount as `Double`.
- `quantity`: ordered quantity.
- `user`: many-to-one relation to `User`, joined by `user_id`.

## Database And Flyway

Configured in `src/main/resources/application.properties`:

- `spring.datasource.url=jdbc:mysql://localhost:3306/order_service`
- `spring.datasource.username=root`
- `spring.datasource.password=root`
- `spring.jpa.hibernate.ddl-auto=none`
- `spring.flyway.enabled=true`
- `spring.flyway.locations=classpath:db/migration`
- `spring.flyway.table=flyway_order_history`

Current migrations:

- `V1__create_user_table.sql`: creates `users` table with unique `email`.
- `V2__create_order_table.sql`: creates `orders` table with foreign key to `users`.
- `V3__update_amount_column.sql`: changes `orders.amount` to `DOUBLE NOT NULL`.

## Kafka Context

Kafka bootstrap server:

- `spring.kafka.bootstrap-servers=localhost:9092`

Produced topic:

- `inventory_order_created`

Topic constant:

- `InventoryTopicConstant.ORDER_CREATED`

Produced event payload:

```json
{
  "productName": "example-product",
  "quantity": 1
}
```

Event DTO:

- `InventoryEvent.productName`
- `InventoryEvent.quantity`

Producer flow:

1. Client calls `POST /api/orders`.
2. `OrderController.createOrder()` delegates to `OrderService.saveOrder()`.
3. `OrderService.saveOrder()` saves the order using `OrderRepository`.
4. `OrderService.createInventoryEvent()` creates an `InventoryEvent` from `Order.productName` and `Order.quantity`.
5. `KafkaInventoryMessageSender.sendMessage()` publishes the event to topic `inventory_order_created`.

## External Dependency

Inventory service URL:

- `inventory.service.url=http://localhost:8080`

The `InventoryService` wrapper uses the configured `inventoryRestTemplate` and currently calls:

- `GET /api/inventory/{productName}`

Note: the current inventory-service controller only exposes `POST /api/inventory`, so verify this contract before using the REST client path.

## Current Behavior Notes

- Order creation saves the order before inventory availability is confirmed.
- The order service does not currently wait for inventory-service confirmation.
- The current flow is asynchronous and event-driven after the order is persisted.
- The order service creates the Kafka topic bean for `inventory_order_created`.
- All order-service Kafka classes are directly under `com.example.order_service.kafka`; do not add nested Kafka subpackages unless this context is updated.
- If changing Kafka topic names, payload fields, or serialization, update this file and coordinate with inventory-service.

## Known Risks To Consider Before Changes

- Invalid or out-of-stock orders may be persisted because stock validation is not enforced before save.
- There is no rollback or compensating action if inventory-service fails after order creation.
- `InventoryService.getProductByName()` may not match the current inventory-service REST API.
- Keep `OrderService` dependencies focused on the active order creation flow to avoid unused bean injection.
