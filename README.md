# Payment Transaction System

A simple payment management REST API built with Spring Boot to learn JWT authentication and Spring Security.

## Overview

This is a learning project I created to understand how authentication and security work in Spring Boot applications. It's a basic payment system where users can register, login, and manage payment transactions.

I built this to learn:
- How JWT tokens work for authentication
- How to encrypt passwords with BCrypt
- How to protect API endpoints
- How Spring Security filters work
- Basic REST API design

**Note**: This is not a real payment system. It uses an in-memory database and simulates payment processing for learning purposes.

## Features

- User registration and login
- JWT token authentication
- Create and manage payments
- Refund payments
- Check payment status
- Protected API endpoints

## Tech Stack

- **Spring Boot 3.2.0** - Main framework
- **Java 17** - Programming language
- **Spring Security** - Security and authentication
- **JWT** - Token-based auth
- **H2 Database** - In-memory database (data resets on restart)
- **Spring Data JPA** - Database operations
- **Maven** - Build tool
- **Lombok** - Reduces boilerplate code

## Security Features

### JWT Authentication
The API uses JWT tokens for authentication. When you login, you get a token that you need to include in the header for accessing protected endpoints.

- Token expires after 24 hours
- Stateless authentication (no sessions stored on server)

### Password Encryption
User passwords are encrypted using BCrypt before saving to database. This means:
- Passwords are never stored in plain text
- Each password gets a unique salt
- Same password produces different hash each time

### Endpoint Protection
- Public endpoints: `/auth/register`, `/auth/login`, `/h2-console`
- Protected endpoints: Everything under `/payments` requires a valid JWT token

## How to Run

### Requirements
- Java 17 or higher
- Maven

### Steps

1. Clone the repository
```bash
git clone https://github.com/Co-ctrl-hash/first-payment-system.git
cd first-payment-system
```

2. Build the project
```bash
mvn clean install
```

3. Run the application
```bash
mvn spring-boot:run
```

4. The server starts on `http://localhost:8081`

### Database Console
You can access the H2 database console at `http://localhost:8081/h2-console`

Connection details:
- JDBC URL: `jdbc:h2:mem:payment_db`
- Username: `sa`
- Password: (leave empty)

## API Endpoints

### Register a new user
```bash
POST /auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "testpass"
}
```

### Login
```bash
POST /auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "testpass"
}
```
Returns a JWT token. Copy this token for subsequent requests.

### Create a payment (requires JWT token)
```bash
POST /payments
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "userId": 1,
  "amount": 500,
  "currency": "USD",
  "paymentMethod": "CREDIT_CARD"
}
```

### Get all payments
```bash
GET /payments
Authorization: Bearer YOUR_JWT_TOKEN
```

### Get payment by ID
```bash
GET /payments/{id}
Authorization: Bearer YOUR_JWT_TOKEN
```

### Refund a payment
```bash
POST /payments/{id}/refund
Authorization: Bearer YOUR_JWT_TOKEN
```

## How Payment Processing Works

The system simulates payment processing:
- Payments have a 75% chance of success
- Payments over 100,000 automatically fail (business rule I added)
- Each payment gets a unique transaction ID like `HD-1-1771573649709`

Payment can have these statuses:
- `INITIATED` - Payment just created
- `SUCCESS` - Payment went through
- `FAILED` - Payment failed
- `REFUNDED` - Payment was refunded

## What I Learned

- Setting up Spring Security with JWT
- Creating custom authentication filters
- Encrypting passwords properly
- Designing REST APIs
- Working with Spring Data JPA
- Exception handling in Spring Boot
- Using constructor injection instead of @Autowired
- Logging with SLF4J

## Future Improvements

Things I want to add when I learn more:
- Add proper unit tests
- Switch to a real database (PostgreSQL)
- Add role-based access (admin vs user)
- Add pagination for payment lists
- Better error messages
- API documentation with Swagger

## Author

**Harsh Deep**

Built this project to learn Spring Boot security and REST API development.

## Notes

- The database is in-memory, so all data is lost when you stop the application
- This is a learning project, not meant for production use
- Payment processing is simulated, not real
