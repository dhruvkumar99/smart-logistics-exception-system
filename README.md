# Smart Logistics Exception Management System

A Spring Boot backend to record, assign, track and resolve shipment exceptions in logistics.

## Tech Stack
- Java 17
- Spring Boot 3.1.5
- Spring Data JPA / Hibernate
- MySQL
- Maven
- Lombok
- Bean Validation

## Features
- User management with roles (ADMIN, OPERATIONS_MANAGER, SUPPORT_AGENT)
- Shipment lifecycle management
- Exception tracking (DELAYED, DAMAGED, LOST, ADDRESS_ISSUE, FAILED_DELIVERY, PAYMENT_ISSUE)
- Automatic priority calculation based on business rules
- Exception assignment to support agents
- Exception history audit trail (who changed what, when)
- REST APIs with validation and global exception handling

## API Endpoints

### Users
- POST   /api/users
- GET    /api/users
- GET    /api/users/{id}
- PUT    /api/users/{id}
- PUT    /api/users/{id}/toggle-active
- DELETE /api/users/{id}

### Shipments
- POST   /api/shipments
- GET    /api/shipments
- GET    /api/shipments/{id}
- PUT    /api/shipments/{id}/status
- DELETE /api/shipments/{id}

### Exceptions
- POST   /api/exceptions
- GET    /api/exceptions
- GET    /api/exceptions/{id}
- GET    /api/exceptions/status/{status}
- GET    /api/exceptions/priority/{priority}
- GET    /api/exceptions/type/{type}
- GET    /api/exceptions/assigned/{userId}
- GET    /api/exceptions/{id}/history
- PUT    /api/exceptions/{id}/assign/{userId}
- PUT    /api/exceptions/{id}/resolve
- PUT    /api/exceptions/{id}/status
- DELETE /api/exceptions/{id}
