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

@Service
public class PaymentService {

    private final TransactionRepository transactionRepository;

    public PaymentService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction processDomesticPayment(DomesticPaymentRequest request) {
        Transaction transaction = Transaction.builder()
                .transactionCode("DOM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .type("DOMESTIC")
                .build();

        return transactionRepository.save(transaction);
    }

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
