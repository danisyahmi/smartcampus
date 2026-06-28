package com.smartcampus.notification.payment.repositories;

import com.smartcampus.notification.payment.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByMatricNo(String matricNo);
}