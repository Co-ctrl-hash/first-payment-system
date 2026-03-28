package com.harshdeep.payment.ai;

import com.harshdeep.payment.entity.Payment;
import com.harshdeep.payment.entity.PaymentStatus;
import com.harshdeep.payment.exception.AIIntegrationException;
import com.harshdeep.payment.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Service for AI-powered payment analytics using OpenAI API.
 * Provides intelligent responses to user queries about payment data.
 * 
 * Features:
 * - Recognizes payment-related queries and provides instant answers
 * - Falls back to OpenAI API for complex queries
 * - Gracefully handles API failures with fallback responses
 */
@Slf4j
@Service
public class AIService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    @Value("${openai.api.key:}")
    private String openAiApiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/completions}")
    private String openAiApiUrl;

    @Value("${openai.model:gpt-3.5-turbo-instruct}")
    private String openAiModel;

    public AIService(PaymentRepository paymentRepository, RestTemplate restTemplate) {
        this.paymentRepository = paymentRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Process user message and generate appropriate response.
     * Recognizes common payment queries and provides instant answers.
     * Falls back to OpenAI API for complex queries.
     * 
     * @param userMessage User's query message
     * @return Response string (formatted answer or error message)
     */
    public String processMessage(@Nullable String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            log.warn("Received empty or null message");
            return "Please provide a valid question.";
        }

        String normalizedMessage = userMessage.trim().toLowerCase(Locale.ENGLISH);
        log.debug("Processing AI message: {}", userMessage);

        // Recognize and quickly answer common queries
        if (isFailedPaymentsQuery(normalizedMessage)) {
            return buildFailedPaymentsResponse();
        }

        if (isSuccessfulAmountQuery(normalizedMessage)) {
            return buildTotalSuccessfulAmountResponse();
        }

        if (isPaymentCountQuery(normalizedMessage)) {
            return buildPaymentCountResponse();
        }

        // Fall back to OpenAI for complex queries
        return callOpenAI(userMessage);
    }

    /**
     * Direct call to OpenAI API for complex analysis.
     * Enriches prompt with current payment data context.
     * 
     * @param message User query
     * @return AI-generated response
     */
    private String callOpenAI(@NonNull String message) {
        if (!isOpenAIConfigured()) {
            log.warn("OpenAI API not configured");
            return "OpenAI integration unavailable. Configure openai.api.key to enable AI responses.";
        }

        try {
            String enrichedPrompt = buildContextualPrompt(message);
            return invokeOpenAIAPI(enrichedPrompt);
        } catch (RestClientException ex) {
            log.error("Error calling OpenAI API", ex);
            throw new AIIntegrationException("Failed to connect to OpenAI", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in AI processing", ex);
            throw new AIIntegrationException("Unexpected error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Invoke OpenAI API with the provided prompt.
     * 
     * @param prompt The prompt to send to OpenAI
     * @return Response text from OpenAI
     */
    @SuppressWarnings("unchecked")
    private String invokeOpenAIAPI(@NonNull String prompt) {
        HttpHeaders headers = createOpenAIHeaders();
        Map<String, Object> payload = createOpenAIPayload(prompt);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(
                openAiApiUrl,
                new HttpEntity<>(payload, headers),
                Map.class
        );

        return extractTextFromResponse(response);
    }

    /**
     * Extract response text from OpenAI API response body.
     * 
     * @param response API response entity
     * @return Extracted text or error message
     */
    private String extractTextFromResponse(@NonNull ResponseEntity<Map> response) {
        Map<String, Object> body = response.getBody();
        
        if (body == null) {
            log.error("OpenAI returned null response body");
            return "Unable to process the query right now.";
        }

        if (body.containsKey("error")) {
            log.error("OpenAI error: {}", body.get("error"));
            return "Unable to process the query right now.";
        }

        List<Object> choices = (List<Object>) body.get("choices");
        if (choices == null || choices.isEmpty()) {
            log.error("OpenAI returned empty choices");
            return "Unable to process the query right now.";
        }

        Map<String, Object> firstChoice = (Map<String, Object>) choices.get(0);
        Object text = firstChoice.get("text");
        
        if (text == null) {
            log.error("OpenAI choice missing text field");
            return "Unable to process the query right now.";
        }

        return text.toString().trim();
    }

    /**
     * Create HTTP headers for OpenAI API request.
     */
    private HttpHeaders createOpenAIHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);
        return headers;
    }

    /**
     * Create request payload for OpenAI API.
     */
    private Map<String, Object> createOpenAIPayload(@NonNull String prompt) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", openAiModel);
        payload.put("prompt", prompt);
        payload.put("max_tokens", 200);
        payload.put("temperature", 0.7);
        return payload;
    }

    /**
     * Build contextual prompt enriched with current payment data.
     */
    private String buildContextualPrompt(@NonNull String userMessage) {
        List<Payment> payments = paymentRepository.findAll();
        String paymentsContext = formatPaymentsForContext(payments);
        
        return "You are a payment analysis assistant. Use the payment data below to answer accurately.\n\n"
                + "Payment Statistics:\n"
                + paymentsContext + "\n\n"
                + "User Question: " + userMessage + "\n\n"
                + "Provide a concise, accurate answer based on the data provided.";
    }

    /**
     * Format payments data for AI context using JSON.
     */
    private String formatPaymentsForContext(@NonNull List<Payment> payments) {
        if (payments.isEmpty()) {
            return "No payments found in system.";
        }

        StringBuilder context = new StringBuilder();
        context.append("Total Payments: ").append(payments.size()).append("\n");
        
        long successCount = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .count();
        context.append("Successful: ").append(successCount).append("\n");

        long failedCount = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.FAILED)
                .count();
        context.append("Failed: ").append(failedCount).append("\n");

        Double totalAmount = paymentRepository.sumAmountByStatus(PaymentStatus.SUCCESS);
        context.append("Total Amount (Success): ").append(totalAmount != null ? totalAmount : "0.0");
        
        return context.toString();
    }

    /**
     * Query: Count and details of failed payments.
     */
    private boolean isFailedPaymentsQuery(@NonNull String message) {
        return (message.contains("failed") && message.contains("payment"))
                || (message.contains("fail") && message.contains("count"));
    }

    private String buildFailedPaymentsResponse() {
        long failedCount = paymentRepository.countByStatus(PaymentStatus.FAILED);
        log.info("Failed payments count: {}", failedCount);
        return "You have " + failedCount + " failed payments in your system.";
    }

    /**
     * Query: Total amount of successful payments.
     */
    private boolean isSuccessfulAmountQuery(@NonNull String message) {
        return (message.contains("total") || message.contains("sum"))
                && message.contains("success")
                && message.contains("amount");
    }

    private String buildTotalSuccessfulAmountResponse() {
        Double total = paymentRepository.sumAmountByStatus(PaymentStatus.SUCCESS);
        double safeTotal = total != null ? total : 0.0;
        log.info("Total successful payment amount: {}", safeTotal);
        return String.format("Total successful payment amount: $%.2f", safeTotal);
    }

    /**
     * Query: Total count of payments.
     */
    private boolean isPaymentCountQuery(@NonNull String message) {
        return (message.contains("how many") || message.contains("count"))
                && message.contains("payment")
                && !message.contains("failed")
                && !message.contains("success");
    }

    private String buildPaymentCountResponse() {
        long totalCount = paymentRepository.count();
        log.info("Total payment count: {}", totalCount);
        return "You have " + totalCount + " payments in total.";
    }

    /**
     * Check if OpenAI is properly configured.
     */
    private boolean isOpenAIConfigured() {
        return openAiApiKey != null && !openAiApiKey.isBlank();
    }
}
