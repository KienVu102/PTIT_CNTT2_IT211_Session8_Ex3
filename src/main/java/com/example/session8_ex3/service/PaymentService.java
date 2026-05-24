package com.example.session8_ex3.service;

import com.example.session8_ex3.annotation.RequireManagerApproval;
import com.example.session8_ex3.annotation.RequireOtp;
import com.example.session8_ex3.dto.DomesticPaymentRequest;
import com.example.session8_ex3.dto.InternationalPaymentRequest;
import com.example.session8_ex3.dto.RefundRequest;
import com.example.session8_ex3.entity.Transaction;
import com.example.session8_ex3.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Payment service handling all three payment operations.
 * NOTE: This service contains NO if-else logic for OTP or role checking.
 * All security cross-cutting concerns are handled by AOP Aspects.
 */
@Service
public class PaymentService {

    private final TransactionRepository transactionRepository;

    public PaymentService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Process a domestic payment. No special security annotation required.
     */
    public Transaction processDomesticPayment(DomesticPaymentRequest request) {
        Transaction transaction = Transaction.builder()
                .transactionCode("DOM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .type("DOMESTIC")
                .build();

        return transactionRepository.save(transaction);
    }

    /**
     * Process an international payment.
     * Annotated with @RequireOtp — the OTP verification is handled entirely by the Aspect.
     */
    @RequireOtp
    public Transaction processInternationalPayment(InternationalPaymentRequest request) {
        Transaction transaction = Transaction.builder()
                .transactionCode("INT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .type("INTERNATIONAL")
                .build();

        return transactionRepository.save(transaction);
    }

    /**
     * Process a refund.
     * Annotated with @RequireManagerApproval — the role check is handled entirely by the Aspect.
     */
    @RequireManagerApproval
    public Transaction processRefund(RefundRequest request) {
        Transaction transaction = Transaction.builder()
                .transactionCode(request.getTransactionCode())
                .amount(request.getAmount())
                .currency("VND")
                .type("REFUND")
                .build();

        return transactionRepository.save(transaction);
    }
}
