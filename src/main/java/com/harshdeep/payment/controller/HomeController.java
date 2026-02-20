package com.harshdeep.payment.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Home controller providing API information and documentation.
 * 
 * @author Harsh Deep
 * @version 1.0.0
 */
@RestController
public class HomeController {

    /**
     * Display HTML homepage with API documentation.
     * 
     * @return HTML page
     */
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String homeHtml() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Secure Payment Service</title>
                    <style>
                        body { font-family: Arial, sans-serif; max-width: 1200px; margin: 50px auto; padding: 20px; background: #f5f5f5; }
                        .container { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                        h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; }
                        h2 { color: #34495e; margin-top: 30px; }
                        .status { display: inline-block; padding: 5px 15px; background: #2ecc71; color: white; border-radius: 20px; font-size: 14px; }
                        .endpoint { background: #ecf0f1; padding: 15px; margin: 10px 0; border-radius: 5px; border-left: 4px solid #3498db; }
                        .method { display: inline-block; padding: 3px 10px; border-radius: 3px; font-weight: bold; color: white; font-size: 12px; margin-right: 10px; }
                        .get { background: #2ecc71; }
                        .post { background: #3498db; }
                        .url { color: #7f8c8d; font-family: monospace; }
                        .db-info { background: #fff3cd; padding: 15px; border-radius: 5px; border-left: 4px solid #ffc107; }
                        .db-item { margin: 5px 0; }
                        .label { font-weight: bold; color: #2c3e50; }
                        .security { background: #d1ecf1; padding: 15px; border-radius: 5px; border-left: 4px solid #0c5460; }
                        a { color: #3498db; text-decoration: none; }
                        a:hover { text-decoration: underline; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>🔐 Secure Payment Service</h1>
                        <p><span class="status">Running</span> <span style="color: #7f8c8d;">Version 1.0.0 | By Harsh Deep</span></p>
                        
                        <h2>📍 API Endpoints</h2>
                        
                        <h3>Authentication (Public)</h3>
                        <div class="endpoint">
                            <span class="method post">POST</span>
                            <span class="url">/auth/register</span> - Register new user
                        </div>
                        <div class="endpoint">
                            <span class="method post">POST</span>
                            <span class="url">/auth/login</span> - Login and get JWT token
                        </div>
                        
                        <h3>Payments (Protected - Requires JWT)</h3>
                        <div class="endpoint">
                            <span class="method post">POST</span>
                            <span class="url">/payments</span> - Create new payment
                        </div>
                        <div class="endpoint">
                            <span class="method get">GET</span>
                            <span class="url">/payments</span> - Get all payments
                        </div>
                        <div class="endpoint">
                            <span class="method get">GET</span>
                            <span class="url">/payments/{id}</span> - Get payment by ID
                        </div>
                        <div class="endpoint">
                            <span class="method get">GET</span>
                            <span class="url">/payments/user/{userId}</span> - Get payments by user ID
                        </div>
                        <div class="endpoint">
                            <span class="method post">POST</span>
                            <span class="url">/payments/{id}/refund</span> - Refund a payment
                        </div>
                        
                        <h2>🔐 Security Features</h2>
                        <div class="security">
                            <div>✓ BCrypt Password Encryption</div>
                            <div>✓ JWT Token Authentication</div>
                            <div>✓ Stateless Session Management</div>
                            <div>✓ Protected API Endpoints</div>
                        </div>
                        
                        <h2>💾 Database</h2>
                        <div class="db-info">
                            <div class="db-item"><span class="label">H2 Console:</span> <a href="/h2-console" target="_blank">http://localhost:8081/h2-console</a></div>
                            <div class="db-item"><span class="label">JDBC URL:</span> <code>jdbc:h2:mem:payment_db</code></div>
                            <div class="db-item"><span class="label">Username:</span> <code>sa</code></div>
                            <div class="db-item"><span class="label">Password:</span> <i>(leave empty)</i></div>
                        </div>
                        
                        <h2>📝 Quick Test</h2>
                        <p>Try these cURL commands:</p>
                        <div class="endpoint">
                            <strong>1. Register User:</strong><br>
                            <code>curl -X POST http://localhost:8081/auth/register -H "Content-Type: application/json" -d "{\\"username\\":\\"user1\\",\\"password\\":\\"pass123\\"}"</code>
                        </div>
                        <div class="endpoint">
                            <strong>2. Login & Get Token:</strong><br>
                            <code>curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\\"username\\":\\"user1\\",\\"password\\":\\"pass123\\"}"</code>
                        </div>
                        <div class="endpoint">
                            <strong>3. Create Payment (with token):</strong><br>
                            <code>curl -X POST http://localhost:8081/payments -H "Authorization: Bearer YOUR_TOKEN" -H "Content-Type: application/json" -d "{\\"userId\\":1,\\"amount\\":100.50,\\"currency\\":\\"USD\\",\\"paymentMethod\\":\\"CREDIT_CARD\\"}"</code>
                        </div>
                    </div>
                </body>
                </html>
                """;
    }

    /**
     * Get API information as JSON.
     * 
     * @return API details map
     */
    @GetMapping("/api")
    public Map<String, Object> apiInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Secure Payment Service");
        response.put("version", "1.0.0");
        response.put("author", "Harsh Deep");
        response.put("status", "Running");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("H2 Console", "http://localhost:8081/h2-console");
        endpoints.put("Register", "POST /auth/register");
        endpoints.put("Login", "POST /auth/login");
        endpoints.put("Create Payment", "POST /payments");
        endpoints.put("Get All Payments", "GET /payments");
        endpoints.put("Get Payment by ID", "GET /payments/{id}");
        endpoints.put("Get Payments by User", "GET /payments/user/{userId}");
        endpoints.put("Refund Payment", "POST /payments/{id}/refund");
        
        response.put("endpoints", endpoints);
        
        Map<String, String> security = new HashMap<>();
        security.put("Password Encryption", "BCrypt");
        security.put("Authentication", "JWT");
        security.put("Session", "Stateless");
        
        response.put("security", security);
        
        Map<String, String> database = new HashMap<>();
        database.put("JDBC URL", "jdbc:h2:mem:payment_db");
        database.put("Username", "sa");
        database.put("Password", "");
        
        response.put("database", database);
        
        return response;
    }
}
