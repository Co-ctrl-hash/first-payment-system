# Secure Payment Service

A lightweight Spring Boot application for payment transaction management with JWT authentication and AI-powered analytics.

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+

### Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

Server runs at `http://localhost:8081`

## API Endpoints

### Authentication (Public)
- `POST /auth/register` - Register user
- `POST /auth/login` - Login (returns JWT token)

### Payments (Protected - requires JWT)
- `POST /payments` - Create payment
- `GET /payments` - Get all payments
- `GET /payments/{id}` - Get payment by ID
- `GET /payments/user/{userId}` - Get user's payments
- `POST /payments/{id}/refund` - Refund payment

### AI Analytics (Protected - requires JWT)
- `POST /ai/chat` - Ask questions about payments

## Configuration

Edit `src/main/resources/application.properties`:
```properties
jwt.secret=your-secret-key (min 32 chars)
jwt.expiration-ms=86400000
openai.api.key=your-api-key (optional)
```

## Database

H2 in-memory database. Access console at `http://localhost:8081/h2-console`
- JDBC URL: `jdbc:h2:mem:payment_db`
- Username: `sa`
- Password: (leave empty)

## Project Structure

```
src/main/java/com/harshdeep/payment/
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Data access (JPA)
├── entity/         # Database models
├── config/         # Spring configuration
├── security/       # JWT authentication
├── ai/             # AI integration
└── exception/      # Error handling
```

## Tech Stack

- Spring Boot 3.2.0 | Java 17 | Maven
- Spring Security + JWT + BCrypt
- JPA + Hibernate + H2

