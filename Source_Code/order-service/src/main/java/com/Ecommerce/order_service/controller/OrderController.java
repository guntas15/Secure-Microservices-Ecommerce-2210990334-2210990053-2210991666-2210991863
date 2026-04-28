package com.Ecommerce.order_service.controller;

import com.Ecommerce.order_service.dto.request.CreateOrderRequest;
import com.Ecommerce.order_service.dto.response.AdminOrderResponse;
import com.Ecommerce.order_service.dto.response.OrderCancelResponse;
import com.Ecommerce.order_service.dto.response.OrderHistoryResponse;
import com.Ecommerce.order_service.dto.response.OrderResponse;
import com.Ecommerce.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAuthority('PLACE_ORDER')")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateOrderRequest request) {

        OrderResponse response =
                orderService.createOrder(request, authHeader);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasAuthority('CANCEL_ORDER')")
    public ResponseEntity<OrderCancelResponse> cancelOrder(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authHeader) {

        return ResponseEntity.ok(
                orderService.cancelOrder(orderId, authHeader)
        );
    }


    @GetMapping("/history")
    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistory() {
        return ResponseEntity.ok(orderService.getOrderHistory());
    }
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminOrderResponse>> getAllOrdersForAdmin() {
        return ResponseEntity.ok(orderService.getAllOrdersForAdmin());
    }
    @PutMapping("/internal/orders/{orderId}/paid")
    public void markOrderAsPaid(@PathVariable Long orderId) {
        orderService.markOrderAsPaid(orderId);
    }

    @PutMapping("/internal/orders/{orderId}/payment-failed")
    public void markOrderAsPaymentFailed(@PathVariable Long orderId) {
        orderService.markOrderAsPaymentFailed(orderId);
    }
    @GetMapping("/internal/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrderByIdInternal(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(
                orderService.getOrderById(orderId, authHeader)
        );
    }









}