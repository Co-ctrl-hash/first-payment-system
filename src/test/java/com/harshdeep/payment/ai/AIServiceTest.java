package com.harshdeep.payment.ai;

import com.harshdeep.payment.entity.Payment;
import com.harshdeep.payment.entity.PaymentStatus;
import com.harshdeep.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AIServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AIService aiService;

    @Test
    void processMessage_shouldReturnFailedPaymentsCount() {
        when(paymentRepository.countByStatus(PaymentStatus.FAILED)).thenReturn(3L);

        String response = aiService.processMessage("How many failed payments do we have?");

        assertEquals("Failed payments count: 3", response);
        verify(paymentRepository).countByStatus(PaymentStatus.FAILED);
    }

    @Test
    void processMessage_shouldReturnTotalSuccessfulAmount() {
        when(paymentRepository.sumAmountByStatus(PaymentStatus.SUCCESS)).thenReturn(1520.5);

        String response = aiService.processMessage("show total successful payment sum");

        assertEquals("Total successful payment amount: 1520.50", response);
        verify(paymentRepository).sumAmountByStatus(PaymentStatus.SUCCESS);
    }

    @Test
    void callOpenAI_shouldReturnUnavailableMessage_whenApiKeyMissing() {
        ReflectionTestUtils.setField(aiService, "openAiApiKey", "");

        String response = aiService.callOpenAI("Any question");

        assertEquals("OpenAI fallback unavailable. Configure openai.api.key to enable AI responses.", response);
    }

    @Test
    void callOpenAI_shouldReturnGeneratedText_whenResponseIsValid() {
        ReflectionTestUtils.setField(aiService, "openAiApiKey", "test-key");
        ReflectionTestUtils.setField(aiService, "openAiApiUrl", "https://api.openai.com/v1/completions");
        ReflectionTestUtils.setField(aiService, "openAiModel", "gpt-3.5-turbo-instruct");

        Payment payment = new Payment();
        payment.setId(10L);
        payment.setUserId(5L);
        payment.setAmount(200.0);
        payment.setStatus(PaymentStatus.SUCCESS);
        when(paymentRepository.findAll()).thenReturn(List.of(payment));

        Map<String, Object> body = Map.of(
                "choices", List.of(Map.of("text", " AI answer from model "))
        );
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(body, HttpStatus.OK));

        String response = aiService.callOpenAI("What should I know?");

        assertEquals("AI answer from model", response);
    }

    @Test
    void callOpenAI_shouldHandleErrorPayload() {
        ReflectionTestUtils.setField(aiService, "openAiApiKey", "test-key");
        ReflectionTestUtils.setField(aiService, "openAiApiUrl", "https://api.openai.com/v1/completions");
        ReflectionTestUtils.setField(aiService, "openAiModel", "gpt-3.5-turbo-instruct");
        when(paymentRepository.findAll()).thenReturn(Collections.emptyList());

        Map<String, Object> body = Map.of("error", Map.of("message", "bad request"));
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(body, HttpStatus.BAD_REQUEST));

        String response = aiService.callOpenAI("Question");

        assertEquals("Unable to process the query right now.", response);
    }
}
