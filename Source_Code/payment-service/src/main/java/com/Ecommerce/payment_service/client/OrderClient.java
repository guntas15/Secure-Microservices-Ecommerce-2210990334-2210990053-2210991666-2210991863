package com.Ecommerce.payment_service.client;


import com.Ecommerce.payment_service.dto.response.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "order-service")
public interface OrderClient {

    @GetMapping("/internal/orders/{orderId}")
    OrderResponse getOrderById(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authHeader
    );
    @PutMapping("/api/orders/internal/orders/{orderId}/paid")
    void markOrderAsPaid(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authHeader
    );

    @PutMapping("/api/orders/internal/orders/{orderId}/payment-failed")
    void markOrderAsPaymentFailed(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authHeader
    );

}


