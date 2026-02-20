# 🔐 Secure Payment Service

A production-ready payment transaction management system built with Spring Boot 3, featuring JWT authentication, BCrypt password encryption, and comprehensive security features.

> **Note**: This is a personal learning project developed to demonstrate secure API development practices and modern backend architecture.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Payment Lifecycle](#payment-lifecycle)
- [Security Features](#security-features)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Author](#author)

## 🎯 Overview

Secure Payment Service is a RESTful API backend system designed to handle payment transactions with enterprise-level security. The system implements JWT-based authentication, BCrypt password hashing, and follows best practices for secure API development.

## ✨ Features

### Core Features
- ✅ User registration and authentication
- ✅ JWT token-based authorization
- ✅ Payment transaction management
- ✅ Payment status tracking (INITIATED, SUCCESS, FAILED, REFUNDED)
- ✅ Payment refund processing
- ✅ User-specific payment retrieval

### Security Features
- 🔐 BCrypt password encryption
- 🔑 JWT token authentication
- 🛡️ Stateless session management
- 🚫 Protected API endpoints
- 📝 Global exception handling

### Code Quality
- 🏗️ Constructor-based dependency injection
- 📊 SLF4J logging throughout
- 🎯 Layered architecture (Controller → Service → Repository)
- ✨ Clean code principles

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| **Framework** | Spring Boot 3.2.0 |
| **Language** | Java 17 |
| **Security** | Spring Security + JWT |
| **Database** | H2 (In-Memory) |
| **ORM** | Spring Data JPA / Hibernate |
| **Build Tool** | Maven |
| **Password Encryption** | BCrypt |
| **Validation** | Jakarta Validation |
| **Utilities** | Lombok |

## 🏛️ Architecture

```
┌─────────────────┐
│   Controllers   │  ← REST API Endpoints
└────────┬────────┘
         │
┌────────▼────────┐
│    Services     │  ← Business Logic & Logging
└────────┬────────┘
         │
┌────────▼────────┐
│  Repositories   │  ← Data Access Layer
└────────┬────────┘
         │
┌────────▼────────┐
│   H2 Database   │  ← In-Memory Storage
└─────────────────┘
```

### Security Flow

```
Request → JWT Filter → Validate Token → Set Authentication → Controller → Service → Repository
```

## 💳 Payment Lifecycle

### Transaction ID Format
`HD-{userId}-{timestamp}`

**Example**: `HD-1-1771573649709`

### Business Rules
- ✅ **75% SUCCESS** - Payment processed successfully (for amounts ≤ 100,000)
- ❌ **25% FAILED** - Random failure (insufficient funds/technical error)
- 🚫 **Auto FAILED** - Payments over 100,000 automatically rejected (exceeds limit)

### Payment Statuses
1. **INITIATED** - Payment request received
2. **SUCCESS** - Payment completed successfully
3. **FAILED** - Payment processing failed
4. **REFUNDED** - Successful payment refunded

## 🔒 Security Features

### 1. Password Security
- Passwords encrypted using **BCrypt** algorithm
- Salt automatically generated per password
- Rainbow table attack prevention

### 2. JWT Authentication
- Token-based stateless authentication
- 24-hour token validity
- Automatic token validation on each request

### 3. Endpoint Protection
- **Public Endpoints**: `/auth/**`, `/h2-console/**`
- **Protected Endpoints**: All `/payments/**` APIs require valid JWT token

### 4. Session Management
- Stateless architecture (no server-side sessions)
- CSRF protection disabled (not needed for JWT)

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Installation & Running

1. **Clone the repository**
```bash
git clone <repository-url>
cd first-payment-system
```

2. **Build the project**
```bash
mvn clean install
```

3. **Run the application**
```bash
mvn spring-boot:run
```

4. **Access the application**
- API Base URL: `http://localhost:8081`
- Web Interface: `http://localhost:8081/`
- H2 Console: `http://localhost:8081/h2-console`

### H2 Database Configuration
- **JDBC URL**: `jdbc:h2:mem:payment_db`
- **Username**: `sa`
- **Password**: *(leave empty)*

## 📚 API Documentation

### Authentication APIs (Public)

#### 1. Register User
```http
POST /auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "password": "secure_password"
}
```

**Response**: User object with encrypted password

#### 2. Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "secure_password"
}
```

**Response**: JWT token (use in Authorization header for protected endpoints)

### Payment APIs (Protected - Requires JWT Token)

All payment endpoints require `Authorization: Bearer <token>` header

#### 3. Create Payment
```http
POST /payments
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "userId": 1,
  "amount": 100.50,
  "currency": "USD",
  "paymentMethod": "CREDIT_CARD"
}
```

**Response**: Payment object with transaction ID and status

#### 4. Get All Payments
```http
GET /payments
Authorization: Bearer <your-jwt-token>
```

#### 5. Get Payment by ID
```http
GET /payments/{id}
Authorization: Bearer <your-jwt-token>
```

#### 6. Get Payments by User
```http
GET /payments/user/{userId}
Authorization: Bearer <your-jwt-token>
```

#### 7. Refund Payment
```http
POST /payments/{id}/refund
Authorization: Bearer <your-jwt-token>
```

**Note**: Only payments with SUCCESS status can be refunded

## 📁 Project Structure

```
src/main/java/com/harshdeep/payment/
├── config/
│   ├── SecurityBeans.java        # BCrypt encoder bean
│   └── SecurityConfig.java       # JWT security configuration
├── controller/
│   ├── AuthController.java       # Authentication endpoints
│   ├── HomeController.java       # Documentation homepage
│   └── PaymentController.java    # Payment transaction endpoints
├── entity/
│   ├── Payment.java             # Payment entity
│   ├── PaymentStatus.java       # Payment status enum
│   └── User.java                # User entity
├── exception/
│   ├── ErrorResponse.java       # Error response DTO
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── repository/
│   ├── PaymentRepository.java   # Payment data access
│   └── UserRepository.java      # User data access
├── security/
│   ├── JwtFilter.java          # JWT authentication filter
│   └── JwtUtil.java            # JWT token utility
├── service/
│   └── PaymentService.java     # Payment business logic
└── PaymentApplication.java     # Main application class
```

## 🧪 Testing Flow

### Step-by-Step Test

1. **Register a User**
```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass"}'
```

2. **Login and Get Token**
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass"}'
```

3. **Create Payment (with token)**
```bash
curl -X POST http://localhost:8081/payments \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"amount":150.75,"currency":"USD","paymentMethod":"CREDIT_CARD"}'
```

4. **Get User Payments**
```bash
curl -X GET http://localhost:8081/payments/user/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🎓 Learning Outcomes

This project demonstrates:
- ✅ RESTful API design principles
- ✅ JWT authentication implementation
- ✅ Spring Security configuration
- ✅ Password encryption best practices
- ✅ Layered architecture
- ✅ Dependency injection patterns
- ✅ Exception handling strategies
- ✅ Logging best practices
- ✅ Code documentation with JavaDoc

## 🔄 Future Enhancements

- [ ] Add role-based authorization (ADMIN, USER)
- [ ] Implement payment gateway integration
- [ ] Add transaction history and audit logs
- [ ] Implement rate limiting
- [ ] Add API versioning
- [ ] Integrate with real database (PostgreSQL/MySQL)
- [ ] Add unit and integration tests
- [ ] Implement caching with Redis
- [ ] Add API documentation with Swagger/OpenAPI

## 👨‍💻 Author

**Harsh Deep**

This project was developed as a personal learning initiative to understand and implement secure backend development practices using modern Java and Spring Boot technologies.

## 📄 License

This is a personal learning project created for educational purposes.

---

**Note**: This project uses an in-memory H2 database. Data will be lost when the application stops. For production use, integrate with a persistent database like PostgreSQL or MySQL.

## 🙏 Acknowledgments

- Spring Boot Documentation
- Spring Security Documentation
- JWT.io for JWT standards
- Baeldung tutorials for Spring Security patterns

---

Made with ❤️ for learning and skill development
