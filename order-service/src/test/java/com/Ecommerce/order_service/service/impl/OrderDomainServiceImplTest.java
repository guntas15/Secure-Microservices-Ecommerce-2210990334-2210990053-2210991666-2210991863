package com.Ecommerce.order_service.service.impl;

import com.Ecommerce.order_service.entity.Order;
import com.Ecommerce.order_service.entity.OrderStatus;
import com.Ecommerce.order_service.exception.OrderNotFoundException;
import com.Ecommerce.order_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderDomainServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderDomainServiceImpl orderDomainService;

    // 9 test cases

    @BeforeEach
    void startingMethod() {
        MockitoAnnotations.openMocks(this);
    }

    private Order buildOrder() {
        return Order.builder()
                .id(1L)
                .userId(10L)
                .productId("prod-1")
                .quantity(2)
                .totalPrice(BigDecimal.valueOf(500))
                .status(OrderStatus.ORDER_PLACED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createOrder_savesAndReturnOrder() {
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderDomainService.createOrder(
                10L,
                "prod-1",
                2,
                BigDecimal.valueOf(500)
        );

        assertNotNull(order);
        assertEquals(OrderStatus.ORDER_PLACED, order.getStatus());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void markOrderAsPaid_updatesStatusAndSave() {
        Order order = buildOrder();

        orderDomainService.markOrderAsPaid(order);

        assertEquals(OrderStatus.PAID, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void markOrderAsPaymentFailed_updatesStatusAndSave() {
        Order order = buildOrder();

        orderDomainService.markOrderAsPaymentFailed(order);

        assertEquals(OrderStatus.PAYMENT_FAILED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void markOrderAsRefunded_updatesStatusAndSave() {
        Order order = buildOrder();

        orderDomainService.markOrderAsRefunded(order);

        assertEquals(OrderStatus.REFUNDED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void getOrder_returnsOrder_whenExists() {
        Order order = buildOrder();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        Order result = orderDomainService.getOrder(1L);

        assertEquals(1L, result.getId());
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrder_throwsException_whenNotFound() {
        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderDomainService.getOrder(1L)
        );
    }

    @Test
    void cancelOrder_updatesStatusAndSave() {
        Order order = buildOrder();

        orderDomainService.cancelOrder(order);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void getOrdersByUser_returnsOrders() {
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(10L))
                .thenReturn(List.of(buildOrder()));

        List<Order> orders = orderDomainService.getOrdersByUser(10L);

        assertEquals(1, orders.size());
        verify(orderRepository)
                .findByUserIdOrderByCreatedAtDesc(10L);
    }

    @Test
    void getAllOrders_returnsOrders() {
        when(orderRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(buildOrder()));

        List<Order> orders = orderDomainService.getAllOrders();

        assertEquals(1, orders.size());
        verify(orderRepository).findAllByOrderByCreatedAtDesc();
    }
}
