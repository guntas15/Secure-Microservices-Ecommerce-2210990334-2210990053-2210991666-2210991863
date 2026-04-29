package com.Ecommerce.order_service.client;

import com.Ecommerce.order_service.dto.request.PaymentRequest;
import com.Ecommerce.order_service.dto.response.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClient {

    @PostMapping("/api/payments")
    PaymentResponse doPayment(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PaymentRequest request
    );
    @GetMapping("/api/payments/{orderId}/refund-exists")
    boolean isRefundAlreadyProcessed(@PathVariable Long orderId);


    @PostMapping("/api/payments/internal/refund/{orderId}")
    PaymentResponse refundPayment(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authHeader
    );

    @GetMapping("/api/payments/{orderId}/payment-exists")
    boolean isPaymentDone(@PathVariable Long orderId);
}

