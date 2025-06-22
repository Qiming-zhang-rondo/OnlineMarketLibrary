# Online Marketplace: Java Pub/Sub Core Library

This repository provides a modular Java library of the core logic and a kafka implementation for the [Online Marketplace Benchmark](https://github.com/diku-dk/EventBenchmark),

The project is structured into two main layers:

- **Platform-independent core module** (`core/`): Encapsulates business logic, event flow handling, and domain interfaces. It is fully decoupled from Kafka, Spring, and other platform-specific frameworks.
- **Kafka-based integration module** (`KafkaSpringbootImplementation/`): Implements runtime support using Kafka, Redis within springboot framework. It includes controllers, event listeners, and persistence logic.

> 📎 For the equivalent **C# implementation**, refer to: [OnlineMarketLibrary_CSharp_Pubsub](https://github.com/Qiming-zhang-rondo/OnlineMarketLibrary_CSharp_Pubsub)

---

## 🔍 Comparison with the C# Implementation

| Layer            | Java Version                                                                 | C# Version                                                                 |
|------------------|--------------------------------------------------------------------------------|----------------------------------------------------------------------------|
| `eventMessaging` | Defines abstract consumer classes (e.g., `AbstractCartConsumer`) to encapsulate deserialization, method calling and common error handling. | This part is implemented within impl layer |
| `model`          | Uses JPA annotations and explicit `@EmbeddedId` to define domain state with composite keys. | State models are defined using composite key. |
| `repository`     | Splits responsibilities into fine-grained interfaces (e.g., `CartItemRepository`, `ProductReplicaRepository`). | Interfaces are often more aggregated (e.g., `ICartRepository`) for simplicity. |

---

## 🧩 Microservices Overview

This core library consists of the following independently deployed services:

- **Cart Service Core**: Manages shopping cart operations and triggers stock reservations.
- **Customer Service Core**: Maintains customer information and preferences.
- **Order Service Core**: Coordinates checkout logic and issues invoices.
- **Payment Provider Service Core**: Simulates third-party payment gateway responses.
- **Payment Service Core**: Processes payment events and notifies dependent services.
- **Product Service Core**: Holds product catalog and price information.
- **Seller Service Core**: Maintains seller profiles and revenue tracking.
- **Shipment Service Core**: Tracks shipment status and delivery notifications.
- **Stock Service Core**: Manages inventory state and reservation status.

---

## 🚀 Getting Started

### Prerequisites

Ensure you have the following installed:

- **Java**: JDK 17 or higher
- **Maven**: Version 3.6 or higher
- **Kafka**: Locally hosted or cloud instance
- **Redis**: Used in some service implementations for caching or session data

### Build the Project

```bash
mvn clean install -DskipTests
```

### Run Kafka Locally

Start a Kafka instance (e.g., via Docker or local script) after ZooKeeper.

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

All services are configured via `application.properties`:

```properties
spring.kafka.bootstrap-servers=localhost:9092
```

### Database

if you want to test other repositories, you need to confihure it on application.properties,like 

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cart
spring.datasource.username=root
spring.datasource.password=your-password
```

---

## Implementation
To use our core library, implement the required interfaces listed below. These interfaces decouple the core logic from the platform-specific runtime.

| **Module**        | **Required Components (Implemented / Extended)**                                                                                                                                                      | **Key Responsibilities**                                                                                                     |
|------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------|
| **Customer**      | `implements ICustomerRepository`<br>`extends AbstractCustomerConsumer`                                                                                                                                | Consumes payment events and updates customer state.                                                                          |
| **Order**         | `implements IOrderRepository`, `IOrderItemRepository`, `IOrderHistoryRepository`, `ICustomerOrderRepository`, `IEventPublisher`<br>`extends AbstractOrderConsumer`                                 | Handles checkout requests, manages order state, and publishes snapshots.                                                    |
| **Payment**       | `implements IOrderPaymentCardRepository`, `IPaymentRepository`, `IEventPublisher`, `IPaymentConfig`<br>`extends AbstractPaymentConsumer`                                                             | Consumes `InvoiceIssued`, executes payments, publishes state marks; supports runtime payment policy configuration.          |
| **PaymentProvider** | `implements IPaymentProviderConfig`                                                                                                                                                                 | Implements provider logic and supports configurable failure rate (e.g., `failPercentage`) for simulation.                   |
| **Product**       | `implements IProductRepository`, `IEventPublisher`<br>`extends AbstractProductConsumer`                                                                                                               | Publishes `ProductUpdated` and `PriceUpdated` events.                                                                        |
| **Shipment**      | `implements IShipmentRepository`, `IPackageRepository`, `IEventPublisher`<br>`extends AbstractShipmentConsumer`                                                                                      | Consumes `PaymentConfirmed`, generates shipments, and sends delivery notifications.                                          |
| **Stock**         | `implements IStockRepository`, `IStockReservationRepository`, `IStockViewRepository`, `IStockViewIndexRepository`, `IEventPublisher`<br>`extends AbstractStockConsumer`                             | Handles stock reservation and replica sync; supports stock view refresh and in-memory logging.                               |
| **Seller**        | `implements ISellerRepository`, `IOrderEntryRepository`, `IOrderSellerViewRepository`, `IMaterializedViewService`<br>`extends AbstractSellerConsumer`                                               | Processes delivery/payment events, updates seller view, invokes view materialization service.                                |

---

## 🤝 Contributions

Contributions and suggestions are welcome! Please fork the repository and submit a pull request.
