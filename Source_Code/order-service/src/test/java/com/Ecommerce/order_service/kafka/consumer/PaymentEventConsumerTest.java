package com.Ecommerce.order_service.kafka.consumer;

import com.Ecommerce.order_service.entity.Order;
import com.Ecommerce.order_service.kafka.event.PaymentEvent;
import com.Ecommerce.order_service.kafka.event.PaymentStatus;
import com.Ecommerce.order_service.service.OrderDomainService;
import com.Ecommerce.order_service.service.OrderService;
import com.Ecommerce.order_service.service.ProductStockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentEventConsumerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderDomainService orderDomainService;

    @Mock
    private ProductStockService productStockService;

    @InjectMocks
    private PaymentEventConsumer paymentEventConsumer;

    // 2 test cases

    @Test
    void consume_marksOrderAsPaid_whenPaymentSuccessful() {


        PaymentEvent event = new PaymentEvent(
                1L,
                PaymentStatus.Success
        );


        paymentEventConsumer.consume(event); // when part

        // then part
        verify(orderService).markOrderAsPaid(1L);
        verifyNoInteractions(orderDomainService);
        verifyNoInteractions(productStockService);
    }

    @Test
    void consume_handlesPaymentFailure_andRestoreStock() {


        PaymentEvent event = new PaymentEvent(
                2L,
                PaymentStatus.Failed
        );

        Order order = Order.builder()
                .id(2L)
                .productId("product-123")
                .quantity(3)
                .build();

        when(orderDomainService.getOrder(2L))
                .thenReturn(order);


        paymentEventConsumer.consume(event);


        verify(orderDomainService).getOrder(2L);
        verify(orderService).markOrderAsPaymentFailed(2L);

        verify(productStockService).restoreStock(
                "product-123",
                3,
                null
        );
    }
}
