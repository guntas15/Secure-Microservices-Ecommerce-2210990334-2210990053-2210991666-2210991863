package com.Ecommerce.order_service.service.impl;

import com.Ecommerce.order_service.client.PaymentClient;
import com.Ecommerce.order_service.dto.request.CreateOrderRequest;
import com.Ecommerce.order_service.dto.response.*;
import com.Ecommerce.order_service.entity.Order;
import com.Ecommerce.order_service.entity.OrderStatus;
import com.Ecommerce.order_service.service.OrderDomainService;
import com.Ecommerce.order_service.service.ProductStockService;
import com.Ecommerce.order_service.service.UserResolverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private ProductStockService productStockService;
    @Mock
    private UserResolverService userResolverService;
    @Mock
    private OrderDomainService orderDomainService;
    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    //10 test cases

    @BeforeEach
    void sTartingMethod() {
        MockitoAnnotations.openMocks(this);
    }

    private Order buildOrder(Long userId) {
        return Order.builder()
                .id(1L)
                .userId(userId)
                .productId("prod-1")
                .quantity(2)
                .totalPrice(BigDecimal.valueOf(200))
                .status(OrderStatus.ORDER_PLACED)
                .createdAt(LocalDateTime.now())
                .build();
    }


    @Test
    void createOrder_createsOrderSuccessfully() {

        CreateOrderRequest request = new CreateOrderRequest("prod-1", 2);

        ProductResponse product = ProductResponse.builder()
                .id("prod-1")
                .price(BigDecimal.valueOf(100))
                .build();

        UserResponse user = UserResponse.builder()
                .id(10L)
                .build();

        Order order = buildOrder(10L);

        when(productStockService.getProduct("prod-1", "token"))
                .thenReturn(product);
        when(userResolverService.resolveCurrentUser("token"))
                .thenReturn(user);
        when(orderDomainService.createOrder(any(), any(), any(), any()))
                .thenReturn(order);

        OrderResponse response =
                orderService.createOrder(request, "token");

        assertEquals("ORDER_PLACED", response.getOrderStatus());
        verify(productStockService).reduceStock("prod-1", 2, "token");
    }


    @Test
    void cancelOrder_cancelsUnpaidOrder() {

        Order order = buildOrder(10L);
        UserResponse user = UserResponse.builder().id(10L).build();

        when(orderDomainService.getOrder(1L)).thenReturn(order);
        when(userResolverService.resolveCurrentUser("token")).thenReturn(user);
        when(paymentClient.isPaymentDone(1L)).thenReturn(false);

        OrderCancelResponse response =
                orderService.cancelOrder(1L, "token");

        assertEquals("CANCELLED", response.getOrderStatus());
        assertEquals("NOT_APPLICABLE", response.getRefundStatus());
        verify(productStockService).restoreStock("prod-1", 2, "token");
    }

    @Test
    void cancelOrder_returnsAlreadyRefunded() {

        Order order = buildOrder(10L);
        UserResponse user = UserResponse.builder().id(10L).build();

        when(orderDomainService.getOrder(1L)).thenReturn(order);
        when(userResolverService.resolveCurrentUser("token")).thenReturn(user);
        when(paymentClient.isPaymentDone(1L)).thenReturn(true);
        when(paymentClient.isRefundAlreadyProcessed(1L)).thenReturn(true);

        OrderCancelResponse response =
                orderService.cancelOrder(1L, "token");

        assertEquals("ALREADY_REFUNDED", response.getRefundStatus());
    }

    @Test
    void cancelOrder_refundsPaidOrder() {

        Order order = buildOrder(10L);
        UserResponse user = UserResponse.builder().id(10L).build();

        PaymentResponse paymentResponse =
                PaymentResponse.builder()
                        .amountPaid(BigDecimal.valueOf(200))
                        .build();

        when(orderDomainService.getOrder(1L)).thenReturn(order);
        when(userResolverService.resolveCurrentUser("token")).thenReturn(user);
        when(paymentClient.isPaymentDone(1L)).thenReturn(true);
        when(paymentClient.isRefundAlreadyProcessed(1L)).thenReturn(false);
        when(paymentClient.refundPayment(1L, "token"))
                .thenReturn(paymentResponse);

        OrderCancelResponse response =
                orderService.cancelOrder(1L, "token");

        assertEquals("REFUNDED", response.getRefundStatus());
        assertEquals(BigDecimal.valueOf(200), response.getRefundedAmount());
    }

    @Test
    void cancelOrder_throwsAccessDenied_forDifferentUser() {

        Order order = buildOrder(10L);
        UserResponse user = UserResponse.builder().id(99L).build();

        when(orderDomainService.getOrder(1L)).thenReturn(order);
        when(userResolverService.resolveCurrentUser("token")).thenReturn(user);

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.cancelOrder(1L, "token")
        );
    }

    @Test
    void getOrderHistory_returnsUserOrders() {

        UserResponse user = UserResponse.builder().id(10L).build();

        when(userResolverService.resolveCurrentUser(null)).thenReturn(user);
        when(orderDomainService.getOrdersByUser(10L))
                .thenReturn(List.of(buildOrder(10L)));

        List<OrderHistoryResponse> history =
                orderService.getOrderHistory();

        assertEquals(1, history.size());
    }

    @Test
    void getAllOrdersForAdmin_returnsAllOrders() {

        when(orderDomainService.getAllOrders())
                .thenReturn(List.of(buildOrder(10L)));

        List<AdminOrderResponse> orders =
                orderService.getAllOrdersForAdmin();

        assertEquals(1, orders.size());
    }


    @Test
    void markOrderAsPaid_redirects() {
        Order order = buildOrder(10L);
        when(orderDomainService.getOrder(1L)).thenReturn(order);

        orderService.markOrderAsPaid(1L);

        verify(orderDomainService).markOrderAsPaid(order);
    }

    @Test
    void markOrderAsPaymentFailed_redirects() {
        Order order = buildOrder(10L);
        when(orderDomainService.getOrder(1L)).thenReturn(order);

        orderService.markOrderAsPaymentFailed(1L);

        verify(orderDomainService).markOrderAsPaymentFailed(order);
    }

    @Test
    void getOrderById_returnsOrderResponse() {
        Order order = buildOrder(10L);
        when(orderDomainService.getOrder(1L)).thenReturn(order);

        OrderResponse response =
                orderService.getOrderById(1L, "token");

        assertEquals(1L, response.getId());
    }
}
