package com.Ecommerce.order_service.client;

import com.Ecommerce.order_service.dto.request.PaymentRequest;
import com.Ecommerce.order_service.dto.response.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClient {

    @PostMapping("/payments")
    PaymentResponse doPayment(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PaymentRequest request
    );
    @GetMapping("/payments/{orderId}/refund-exists")
    boolean isRefundAlreadyProcessed(@PathVariable Long orderId);


    @PostMapping("/payments/internal/refund/{orderId}")
    PaymentResponse refundPayment(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authHeader
    );

    @GetMapping("/payments/{orderId}/payment-exists")
    boolean isPaymentDone(@PathVariable Long orderId);
}

