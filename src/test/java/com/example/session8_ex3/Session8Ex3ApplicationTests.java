package com.example.session8_ex3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class Session8Ex3ApplicationTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testDomesticPayment_Success() throws Exception {
        String requestBody = """
                {
                    "amount": 500000.0,
                    "currency": "VND"
                }
                """;

        mockMvc.perform(post("/api/payments/domestic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Domestic payment processed successfully"))
                .andExpect(jsonPath("$.transaction.amount").value(500000.0))
                .andExpect(jsonPath("$.transaction.currency").value("VND"))
                .andExpect(jsonPath("$.transaction.type").value("DOMESTIC"));
    }

    @Test
    void testInternationalPayment_Success() throws Exception {
        String requestBody = """
                {
                    "amount": 200.0,
                    "currency": "USD"
                }
                """;

        mockMvc.perform(post("/api/payments/international")
                        .header("X-OTP", "123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("International payment processed successfully"))
                .andExpect(jsonPath("$.transaction.amount").value(200.0))
                .andExpect(jsonPath("$.transaction.currency").value("USD"))
                .andExpect(jsonPath("$.transaction.type").value("INTERNATIONAL"));
    }

    @Test
    void testInternationalPayment_MissingOtp_Forbidden() throws Exception {
        String requestBody = """
                {
                    "amount": 200.0,
                    "currency": "USD"
                }
                """;

        mockMvc.perform(post("/api/payments/international")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Security Violation"))
                .andExpect(jsonPath("$.message").value("OTP verification failed: Invalid or missing OTP. Transaction rejected."));
    }

    @Test
    void testInternationalPayment_WrongOtp_Forbidden() throws Exception {
        String requestBody = """
                {
                    "amount": 200.0,
                    "currency": "USD"
                }
                """;

        mockMvc.perform(post("/api/payments/international")
                        .header("X-OTP", "999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Security Violation"))
                .andExpect(jsonPath("$.message").value("OTP verification failed: Invalid or missing OTP. Transaction rejected."));
    }

    @Test
    void testRefund_Success_WithManager() throws Exception {
        String requestBody = """
                {
                    "transactionCode": "TXN999",
                    "amount": 100.0
                }
                """;

        mockMvc.perform(post("/api/payments/refund")
                        .header("X-Role", "MANAGER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Refund processed successfully"))
                .andExpect(jsonPath("$.transaction.transactionCode").value("TXN999"))
                .andExpect(jsonPath("$.transaction.amount").value(100.0))
                .andExpect(jsonPath("$.transaction.type").value("REFUND"));
    }

    @Test
    void testRefund_MissingRole_Forbidden() throws Exception {
        String requestBody = """
                {
                    "transactionCode": "TXN999",
                    "amount": 100.0
                }
                """;

        mockMvc.perform(post("/api/payments/refund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Security Violation"))
                .andExpect(jsonPath("$.message").value("Security Error: Access denied. Only users with MANAGER role can process refunds."));
    }

    @Test
    void testRefund_WrongRole_Forbidden() throws Exception {
        String requestBody = """
                {
                    "transactionCode": "TXN999",
                    "amount": 100.0
                }
                """;

        mockMvc.perform(post("/api/payments/refund")
                        .header("X-Role", "USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Security Violation"))
                .andExpect(jsonPath("$.message").value("Security Error: Access denied. Only users with MANAGER role can process refunds."));
    }

    @Test
    void testRefund_SqlInjection_BadRequest() throws Exception {
        String requestBody = """
                {
                    "transactionCode": "TXN999' OR '1'='1",
                    "amount": 100.0
                }
                """;

        mockMvc.perform(post("/api/payments/refund")
                        .header("X-Role", "MANAGER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.details").value("transactionCode: Transaction code contains invalid characters. Only alphanumeric characters are allowed."));
    }
}
