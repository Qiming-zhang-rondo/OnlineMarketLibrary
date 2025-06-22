# Online Marketplace: Java Pub/Sub Core Library

This repository provides a modular Java library of the core logic and a kafka implementation for the [Online Marketplace Benchmark](https://github.com/diku-dk/EventBenchmark),

The project is structured into two main layers:

- **Platform-independent core module** (`core/`): Encapsulates business logic, event flow handling, and domain interfaces. It is fully decoupled from Kafka, Spring, and other platform-specific frameworks.
- **Kafka-based integration module** (`KafkaSpringbootImplementation/`): Implements runtime support using Kafka, Redis, and JPA. It includes controllers, event listeners, and persistence logic.

> 📎 For the equivalent **C# implementation**, refer to: [OnlineMarketLibrary_CSharp_Pubsub](https://github.com/Qiming-zhang-rondo/OnlineMarketLibrary_CSharp_Pubsub)

---

## 🔍 Comparison with the C# Implementation

| Layer            | Java Version                                                                 | C# Version                                                                 |
|------------------|--------------------------------------------------------------------------------|----------------------------------------------------------------------------|
| `eventMessaging` | Defines abstract consumer classes (e.g., `AbstractCartConsumer`) to encapsulate deserialization and common error handling. | Events are received via platform-level controller interfaces, with logic handled directly in core services. |
| `model`          | Uses JPA annotations and explicit `@EmbeddedId` to define domain state with composite keys. | State models are defined using nested classes; structure is more compact. |
| `repository`     | Splits responsibilities into fine-grained interfaces (e.g., `CartItemRepository`, `ProductReplicaRepository`). | Interfaces are often more aggregated (e.g., `ICartRepository`) for simplicity. |

---

## 🧩 Microservices Overview

This system consists of the following independently deployed services:

- **Cart Service**: Manages shopping cart operations and triggers stock reservations.
- **Customer Service**: Maintains customer information and preferences.
- **Order Service**: Coordinates checkout logic and issues invoices.
- **Payment Provider Service**: Simulates third-party payment gateway responses.
- **Payment Service**: Processes payment events and notifies dependent services.
- **Product Service**: Holds product catalog and price information.
- **Seller Service**: Maintains seller profiles and revenue tracking.
- **Shipment Service**: Tracks shipment status and delivery notifications.
- **Stock Service**: Manages inventory state and reservation status.

---

## 🚀 Getting Started

### Prerequisites

Ensure you have the following installed:

- **Java**: JDK 17 or higher
- **Maven**: Version 3.6 or higher
- **Kafka**: Locally hosted or cloud instance
- **MySQL**: For persistent state storage
- **Redis**: Used in some service implementations for caching or session data

### Build the Project

```bash
mvn clean install -DskipTests
```

### Run Kafka Locally

Start a Kafka instance (e.g., via Docker or local script) with ZooKeeper.

### Start Services

Each service is an individual Spring Boot application. Example:

```bash
cd KafkaSpringbootImplementation/cartService
mvn spring-boot:run
```

Repeat for other services.

---

## ⚙️ Configuration Notes

### Kafka

All services are configured via `application.yml` or `application.properties`:

```properties
spring.kafka.bootstrap-servers=localhost:9092
```

### Database

Use MySQL and configure the connection per service:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cart
spring.datasource.username=root
spring.datasource.password=your-password
```

---

## 📬 Event Flow Example

Example flow for a typical checkout process:

1. Product emits `PriceUpdate` → Cart updates internal state
2. Cart emits `ReserveInventory` → Stock reserves quantity
3. Stock emits `StockConfirmed` → Order continues processing
4. Order emits `InvoiceIssued` → Payment processes invoice
5. Payment emits `PaymentConfirmed` → Triggers shipment and customer notification

Each event is transmitted asynchronously via Kafka and handled by abstracted consumer logic.

---

## 🔭 Future Work

- Add centralized testing infrastructure using TestContainers or MockKafka.
- Enhance observability using Micrometer + Prometheus/Grafana.
- Support additional messaging middleware (e.g., Pulsar, RabbitMQ).
- Evaluate consistency and fault-tolerance using benchmark tools.

---

## 🤝 Contributions

Contributions and suggestions are welcome! Please fork the repository and submit a pull request.
