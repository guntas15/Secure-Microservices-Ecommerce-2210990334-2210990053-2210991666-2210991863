package com.Ecommerce.payment_service.service;

import com.Ecommerce.payment_service.dto.request.PaymentRequest;
import com.Ecommerce.payment_service.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    // PAYMENT MAKER
    PaymentResponse doPayment(PaymentRequest request, String authHeader);

    // REFUND PAYMENT
    PaymentResponse refundPayment(Long orderId,  String authHeader);

   // PAYMENT CHECK
    boolean isPaymentDone(Long orderId);

    //REFUND CHECK
    boolean isRefundAlreadyProcessed(Long orderId);


    // INTERNAL: used by Order Service
    PaymentResponse getPaymentDetailsByOrderId(Long orderId);

    // USER-SAFE
    PaymentResponse getPaymentForUser(Long orderId, Long userId, String authHeader);

    // ADMIN
    List<PaymentResponse> getAllPaymentsForAdmin();
}
