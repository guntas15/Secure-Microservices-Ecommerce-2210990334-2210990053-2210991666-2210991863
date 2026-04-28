package com.Ecommerce.order_service.controller;

import com.Ecommerce.order_service.dto.response.OrderInternalResponse;
import com.Ecommerce.order_service.entity.Order;
import com.Ecommerce.order_service.service.OrderDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/orders")
@RequiredArgsConstructor
public class OrderInternalController {

    private final OrderDomainService orderDomainService;

    @GetMapping("/{orderId}")
    public OrderInternalResponse getOrderInternal(@PathVariable Long orderId) {

        Order order = orderDomainService.getOrder(orderId);

        return OrderInternalResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus().name())
                .build();
    }
}

