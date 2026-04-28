package com.Ecommerce.payment_service.repository;

import com.Ecommerce.payment_service.entity.Payment;

import com.Ecommerce.payment_service.entity.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
    Optional<Payment> findByOrderIdAndType(Long orderId, PaymentType type);
}

