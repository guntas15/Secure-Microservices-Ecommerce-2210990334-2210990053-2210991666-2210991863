package com.Ecommerce.order_service.service.impl;

import com.Ecommerce.order_service.entity.Order;
import com.Ecommerce.order_service.entity.OrderStatus;
import com.Ecommerce.order_service.exception.OrderNotFoundException;
import com.Ecommerce.order_service.repository.OrderRepository;
import com.Ecommerce.order_service.service.OrderDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDomainServiceImpl implements OrderDomainService {

    private final OrderRepository orderRepository;

    @Override
    public Order createOrder(
            Long userId,
            String productId,
            Integer quantity,
            BigDecimal totalPrice
    ) {
        Order order = Order.builder()
                .userId(userId)
                .productId(productId)
                .quantity(quantity)
                .totalPrice(totalPrice)
                .status(OrderStatus.ORDER_PLACED)
                .createdAt(LocalDateTime.now())
                .build();

        return orderRepository.save(order);
    }

    public void markOrderAsPaid(Order order) {
        order.markPaid();
        orderRepository.save(order);
    }

    public void markOrderAsPaymentFailed(Order order) {
        order.markPaymentFailed();
        orderRepository.save(order);
    }
    public void markOrderAsRefunded(Order order) {
        order.markRefunded();
        orderRepository.save(order);
    }


    @Override
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }


    @Override
    public void cancelOrder(Order order) {
        order.cancel();
        orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }
}
