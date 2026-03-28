package com.harshdeep.payment.controller;

import com.harshdeep.payment.ai.AIService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for AI-powered payment analytics.
 * Provides endpoints to chat with OpenAI to analyze payment data.
 * All endpoints require JWT authentication.
 */
@Slf4j
@RestController
@RequestMapping("/ai")
@Validated
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    /**
     * Process a user message and return AI-generated response.
     * The AI analyzes payment data to answer user queries.
     * 
     * Examples:
     * - "How many failed payments do we have?"
     * - "What is the total amount of successful payments?"
     * 
     * @param request Chat request containing the user message
     * @return AI-generated response string
     */
    @PostMapping("/chat")
    public ResponseEntity<String> chat(@Valid @RequestBody ChatRequest request) {
        log.info("Processing AI chat request");
        log.debug("User message: {}", request.message());
        
        String response = aiService.processMessage(request.message());
        
        log.debug("AI response generated successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * DTO for chat request payload.
     * Message is required and must not be blank.
     */
    public record ChatRequest(
            @NotBlank(message = "Message is required and must not be blank")
            String message
    ) {}
}
