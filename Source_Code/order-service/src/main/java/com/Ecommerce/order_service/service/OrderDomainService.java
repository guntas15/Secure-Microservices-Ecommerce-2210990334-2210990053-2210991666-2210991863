package com.Ecommerce.order_service.service;

import com.Ecommerce.order_service.entity.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderDomainService {

    Order createOrder(
            Long userId, String productId, Integer quantity, BigDecimal totalPrice);

    Order getOrder(Long orderId);

    void cancelOrder(Order order);

    void markOrderAsPaid(Order order);

    void markOrderAsPaymentFailed(Order order);

    void markOrderAsRefunded(Order order);

    List<Order> getOrdersByUser(Long userId);

    List<Order> getAllOrders();
}
