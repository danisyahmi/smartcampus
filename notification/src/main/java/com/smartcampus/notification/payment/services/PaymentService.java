package com.smartcampus.notification.payment.services;

import com.smartcampus.notification.payment.models.Payment;
import com.smartcampus.notification.payment.dto.PaymentRequest;
import com.smartcampus.notification.payment.repositories.PaymentRepository;
import com.smartcampus.notification.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    // Harga tetap — ROOM = RM5 per bilik, BOOK = RM3 per buku
    private static final double ROOM_PRICE = 5.0;
    private static final double BOOK_PRICE = 3.0;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private NotificationService notificationService;

    private double calculateDefaultAmount(String bookingType) {
        if ("ROOM".equalsIgnoreCase(bookingType)) {
            return ROOM_PRICE;
        } else if ("BOOK".equalsIgnoreCase(bookingType)) {
            return BOOK_PRICE;
        }
        return 0.0;
    }

    public Payment processPayment(PaymentRequest request) {
        // Kira amount secara auto jika tak dihantar atau 0
        double amount;
        if (request.getAmount() == null || request.getAmount() <= 0) {
            amount = calculateDefaultAmount(request.getBookingType());
        } else {
            amount = request.getAmount();
        }

        Payment payment = new Payment();
        payment.setBookingType(request.getBookingType());
        payment.setBookingId(request.getBookingId());
        payment.setAmount(amount);
        payment.setMatricNo(request.getMatricNo());
        payment.setPaymentDate(LocalDateTime.now());

        if (request.isSuccessSimulation()) {
            payment.setStatus("COMPLETE");
            String message = "Payment of RM" + String.format("%.2f", amount)
            + " is COMPLETE for " + request.getBookingType()
            + " [" + request.getResourceId() + "].";
            notificationService.sendNotification("PAYMENT_SUCCESS", message, request.getMatricNo());
        } else {
            payment.setStatus("PENDING");
            String message = "Payment of RM" + String.format("%.2f", amount)
            + " is PENDING for " + request.getBookingType()
            + " [" + request.getResourceId() + "]. Please retry.";
            notificationService.sendNotification("PAYMENT_PENDING", message, request.getMatricNo());
        }

        return paymentRepository.save(payment);
    }
}