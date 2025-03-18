# Kafka E-Commerce Project

This repository contains the **Kafka E-Commerce Project**, a microservices-based e-commerce system built with Spring Boot, Kafka, and MySQL. The system handles various functionalities like product management, order processing, stock management, and customer sessions.

## Features

- **Microservices Architecture**: Modularized services for scalability and maintainability.
- **Kafka Integration**: Real-time messaging for asynchronous communication between services.
- **MySQL**: Relational database for data persistence.
- **Redis (Planned)**: Used as a caching layer for improved performance in critical operations.
- **Spring Boot**: Simplifies development and deployment.

## Microservices

The project includes the following microservices:

1. **Cart Service**: Manages shopping cart operations.
2. **Customer Service**: Handles customer-related functionalities.
3. **Order Service**: Manages order placement and processing.
4. **Payment Provider Service**: Simulates payment gateway interactions.
5. **Payment Service**: Handles internal payment processing.
6. **Product Service**: Manages product catalog.
7. **Seller Service**: Handles seller operations and inventory.
8. **Shipment Service**: Manages shipping and delivery.
9. **Stock Service**: Handles stock and inventory management.

## Prerequisites

Ensure you have the following installed:

- **Java**: JDK 8 or higher
- **Maven**: Version 3.6 or higher
- **MySQL**: For database operations
- **Kafka**: For messaging between services

## Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/Qiming-zhang-rondo/kafka-ecommerce.git
cd kafka-ecommerce-parent
```

### 2. Build the Project
```bash
mvn clean package -DskipTests
```

### 3. Run the Services
Each service is packaged as a Spring Boot application. You can run them individually

### 4. Access the Services
- Each service runs on a unique port.

## Configuration

### Database
The application uses MySQL for data storage. Update the `application.properties` or `application.yml` for each service with your database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your-database
spring.datasource.username=your-username
spring.datasource.password=your-password
```

### Kafka
Update Kafka broker configurations in `application.properties`:
```properties
spring.kafka.bootstrap-servers=localhost:9092
```

## Future Enhancements

- **Redis Caching**: Enhance performance by caching frequently accessed data.
- **Docker Support**: Containerize all microservices for easier deployment.
- **Monitoring**: Add tools like Prometheus and Grafana for monitoring.

## Contributing

Contributions are welcome! Please fork this repository, make changes, and submit a pull request.


