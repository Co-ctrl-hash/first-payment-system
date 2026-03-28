# Secure Payment Service

A lightweight Spring Boot application for payment transaction management with JWT authentication and AI-powered analytics.

## ✨ Features

- ✅ **User Authentication** - Register and login with secure JWT tokens
- ✅ **JWT Authentication** - Stateless token-based authorization (24-hour expiration)
- ✅ **Secure Passwords** - BCrypt hashing with strength 10
- ✅ **Payment Management** - Create, retrieve, and manage payment transactions
- ✅ **Refund Processing** - Process refunds for successful payments
- ✅ **AI Analytics** - Chat interface for payment insights and analysis
- ✅ **Global Error Handling** - Consistent API error responses
- ✅ **Input Validation** - Jakarta validation on all requests
- ✅ **Structured Logging** - SLF4J/Logback for debugging and monitoring
- ✅ **H2 Console** - Real-time database inspection during development

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+

### Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

Server runs at `http://localhost:8081`

## 📝 Usage Examples

### 1. Register User
```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","password":"SecurePass@123"}'
```

### 2. Login & Get JWT Token
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","password":"SecurePass@123"}'

# Response: JWT token (save this for authenticated requests)
```

### 3. Create Payment (Requires JWT)
```bash
curl -X POST http://localhost:8081/payments \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"amount":100.50,"currency":"USD","paymentMethod":"CREDIT_CARD"}'
```

### 4. Get All Payments
```bash
curl -X GET http://localhost:8081/payments \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 5. Query AI Analytics
```bash
curl -X POST http://localhost:8081/ai/chat \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"message":"How many failed payments do we have?"}'
```

## 🔐 Security & Authentication

### JWT Token Details
- **Algorithm:** HS256 (HMAC-SHA256)
- **Expiration:** 24 hours from token generation
- **Storage:** Pass in `Authorization: Bearer <token>` header
- **Refresh:** Users must login again after expiration

### Password Security
- **Algorithm:** BCrypt with strength 10
- **Unique Salts:** Each password gets unique salt value
- **One-way:** Passwords cannot be decrypted
- **Validation:** Jakarta Bean Validation on all inputs

### Protected Endpoints
- All `/payments/**` endpoints require valid JWT
- All `/ai/**` endpoints require valid JWT
- Public: `/auth/register`, `/auth/login`, `/`

## ## API Endpoints

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

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=PaymentServiceTest
mvn test -Dtest=AIServiceTest

# Run with coverage report
mvn test jacoco:report
```

## ⚙️ Configuration

### Environment Variables (Recommended)
```bash
# JWT Configuration
export JWT_SECRET="your-secret-key-min-32-chars-recommended"
export JWT_EXPIRATION_MS="86400000"  # 24 hours in milliseconds

# OpenAI Configuration (optional)
export OPENAI_API_KEY="sk-your-openai-api-key"
```

### application.properties
```properties
# Server
server.port=8081

# JWT
jwt.secret=your-secret-key (min 32 chars)
jwt.expiration-ms=86400000

# OpenAI (optional)
openai.api.key=your-api-key
openai.api.url=https://api.openai.com/v1/completions
openai.model=gpt-3.5-turbo-instruct

# Database
spring.datasource.url=jdbc:h2:mem:payment_db
spring.h2.console.enabled=true
```

## 💾 Database

H2 in-memory database. Access console at `http://localhost:8081/h2-console`
- **JDBC URL:** `jdbc:h2:mem:payment_db`
- **Username:** `sa`
- **Password:** (leave empty)

### Tables
- **users** - User accounts with BCrypt-hashed passwords
- **payments** - Payment transactions with status tracking

### Payment Statuses
- `INITIATED` - Payment created
- `SUCCESS` - Payment succeeded
- `FAILED` - Payment failed
- `REFUNDED` - Refund processed

## 📦 Project Structure

```
src/main/java/com/harshdeep/payment/
├── PaymentApplication.java           # Spring Boot entry point
├── ai/
│   └── AIService.java                # OpenAI integration
├── config/
│   ├── SecurityBeans.java            # Bean configuration
│   └── SecurityConfig.java           # Spring Security setup
├── controller/
│   ├── AuthController.java           # Auth endpoints
│   ├── PaymentController.java        # Payment endpoints
│   ├── AIController.java             # AI endpoints
│   └── HomeController.java           # Home page
├── entity/
│   ├── Payment.java                  # Payment model
│   ├── User.java                     # User model
│   ├── PaymentStatus.java            # Status enum
│   └── ErrorResponse.java            # Error DTO
├── exception/
│   ├── GlobalExceptionHandler.java   # Exception handling
│   ├── ResourceNotFoundException.java
│   ├── AIIntegrationException.java
│   └── PaymentOperationException.java
├── repository/
│   ├── PaymentRepository.java        # Payment JPA
│   └── UserRepository.java           # User JPA
├── security/
│   ├── JwtUtil.java                  # JWT operations
│   └── JwtFilter.java                # JWT filter
└── service/
    ├── PaymentService.java           # Payment logic
    └── AIService.java                # AI logic

src/main/resources/
├── application.properties            # Configuration
└── application.yml                   # YAML config

src/test/java/com/harshdeep/payment/
├── service/PaymentServiceTest.java   # Service tests
└── ai/AIServiceTest.java             # AI tests
```

## 🔧 Tech Stack

- **Framework:** Spring Boot 3.2.0
- **Language:** Java 17 LTS
- **Build Tool:** Maven 3.6+
- **Security:** Spring Security + JWT + BCrypt
- **Database:** JPA + Hibernate + H2
- **Logging:** SLF4J + Logback
- **Validation:** Jakarta Bean Validation
- **Testing:** JUnit 5 + Mockito

## ❓ Troubleshooting

### Port Already in Use
```bash
# Change port in application.properties
server.port=8082

# Or kill process on port 8081 (Windows)
netstat -ano | findstr :8081
taskkill /PID <PID> /F
```

### JWT Token Expired
- Tokens expire after 24 hours
- Login again to receive a new token
- Check `jwt.expiration-ms` in configuration

### H2 Console Not Accessible
- Ensure `spring.h2.console.enabled=true` in properties
- Access at `http://localhost:8081/h2-console`

### OpenAI Integration Not Working
- Set `OPENAI_API_KEY` environment variable
- Check API key validity on OpenAI dashboard
- Verify API endpoint URL is correct

### Database Issues
- H2 is in-memory, data resets on restart
- For persistence, switch to PostgreSQL/MySQL
- See application.properties for JDBC URL

## 📄 License & Author

**Project:** Secure Payment Service  
**Version:** 1.0.0  
**Author:** Harsh Deep  
**License:** Educational Purpose  
**Repository:** [GitHub](https://github.com/Co-ctrl-hash/first-payment-system)


