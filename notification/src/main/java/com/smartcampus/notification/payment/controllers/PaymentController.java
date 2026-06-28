package com.smartcampus.notification.payment.controllers;

import com.smartcampus.notification.payment.models.Payment;
import com.smartcampus.notification.payment.dto.PaymentRequest;
import com.smartcampus.notification.payment.services.PaymentService;
import com.smartcampus.notification.payment.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    // POST /api/payments/process — proses bayaran
    @PostMapping("/process")
    public ResponseEntity<Payment> processPayment(@RequestBody PaymentRequest request) {
        Payment savedPayment = paymentService.processPayment(request);
        return ResponseEntity.ok(savedPayment);
    }

    // GET /api/payments — semua bayaran
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentRepository.findAll());
    }

    // GET /api/payments/student/{matricNo} — bayaran by student
    @GetMapping("/student/{matricNo}")
    public ResponseEntity<List<Payment>> getByStudent(@PathVariable String matricNo) {
        return ResponseEntity.ok(paymentRepository.findByMatricNo(matricNo));
    }
}