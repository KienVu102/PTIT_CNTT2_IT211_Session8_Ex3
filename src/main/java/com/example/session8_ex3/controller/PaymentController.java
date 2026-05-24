package com.example.session8_ex3.controller;

import com.example.session8_ex3.dto.DomesticPaymentRequest;
import com.example.session8_ex3.dto.InternationalPaymentRequest;
import com.example.session8_ex3.dto.RefundRequest;
import com.example.session8_ex3.entity.Transaction;
import com.example.session8_ex3.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * POST /api/payments/domestic
     * Domestic payment — no special security headers required.
     */
    @PostMapping("/domestic")
    public ResponseEntity<?> domesticPayment(@Valid @RequestBody DomesticPaymentRequest request) {
        Transaction transaction = paymentService.processDomesticPayment(request);
        return ResponseEntity.ok(Map.of(
                "message", "Domestic payment processed successfully",
                "transaction", transaction
        ));
    }

    /**
     * POST /api/payments/international
     * International payment — requires X-OTP header (validated by AOP Aspect).
     */
    @PostMapping("/international")
    public ResponseEntity<?> internationalPayment(@Valid @RequestBody InternationalPaymentRequest request) {
        Transaction transaction = paymentService.processInternationalPayment(request);
        return ResponseEntity.ok(Map.of(
                "message", "International payment processed successfully",
                "transaction", transaction
        ));
    }

    /**
     * POST /api/payments/refund
     * Refund — requires X-Role header with value "MANAGER" (validated by AOP Aspect).
     */
    @PostMapping("/refund")
    public ResponseEntity<?> refundPayment(@Valid @RequestBody RefundRequest request) {
        Transaction transaction = paymentService.processRefund(request);
        return ResponseEntity.ok(Map.of(
                "message", "Refund processed successfully",
                "transaction", transaction
        ));
    }
}
