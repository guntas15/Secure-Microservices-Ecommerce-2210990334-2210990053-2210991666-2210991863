package com.Ecommerce.order_service.kafka.consumer;

import com.Ecommerce.order_service.entity.Order;
import com.Ecommerce.order_service.kafka.event.PaymentEvent;
import com.Ecommerce.order_service.kafka.event.PaymentStatus;
import com.Ecommerce.order_service.service.OrderDomainService;
import com.Ecommerce.order_service.service.OrderService;
import com.Ecommerce.order_service.service.ProductStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {
    private final OrderService orderService;
    private final OrderDomainService orderDomainService;
    private final ProductStockService productStockService;

    @KafkaListener (
            topics= "payment-events",
            groupId= "order-service-group"
    )
    public  void consume(PaymentEvent event){
         log.info("Receive payment event {}", event);

         if(event.getStatus() == PaymentStatus.Success){
             orderService.markOrderAsPaid(event.getOrderId());
         }else{
             // load order details
             Order order = orderDomainService.getOrder(event.getOrderId());

             //Mark order FAILED
             orderService.markOrderAsPaymentFailed(event.getOrderId());

             //STOCK RESTORATION
             productStockService.restoreStock(
                     order.getProductId(),
                     order.getQuantity(),
                     null
             );
         }
    }
}
