package com.Ecommerce.order_service.service.impl;

import com.Ecommerce.order_service.client.PaymentClient;
import com.Ecommerce.order_service.dto.request.CreateOrderRequest;
import com.Ecommerce.order_service.dto.response.*;
import com.Ecommerce.order_service.entity.Order;
import com.Ecommerce.order_service.entity.OrderStatus;
import com.Ecommerce.order_service.service.OrderDomainService;
import com.Ecommerce.order_service.service.OrderService;
import com.Ecommerce.order_service.service.ProductStockService;
import com.Ecommerce.order_service.service.UserResolverService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ProductStockService productStockService;
    private final UserResolverService userResolverService;
    private final OrderDomainService orderDomainService;
    private final PaymentClient paymentClient;
    //  CREATE ORDER

    @Override
    public OrderResponse createOrder(CreateOrderRequest request, String authHeader) {

        // PRODUCT VALIDATION
        ProductResponse product =
                productStockService.getProduct(
                        request.getProductId(),
                        authHeader
                );

        productStockService.reduceStock(
                request.getProductId(),
                request.getQuantity(),
                authHeader
        );

        // PRICE CALCULATION
        BigDecimal totalPrice =
                product.getPrice()
                        .multiply(BigDecimal.valueOf(request.getQuantity()));

        // USER RESOLUTION
        UserResponse user =
                userResolverService.resolveCurrentUser(authHeader);

        // CREATE ORDER
        Order order =
                orderDomainService.createOrder(
                        user.getId(),
                        request.getProductId(),
                        request.getQuantity(),
                        totalPrice
                );


        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .orderStatus("ORDER_PLACED")
                .paymentStatus("PAYMENT_PENDING")
                .createdAt(order.getCreatedAt())
                .build();
    }


    // CANCEL ORDER

    @Override
    @Transactional
    public OrderCancelResponse cancelOrder(Long orderId, String authHeader) {

        //  Load order
        Order order = orderDomainService.getOrder(orderId);

        //  Verify ownership
        UserResponse user =
                userResolverService.resolveCurrentUser(authHeader);

        if (!order.getUserId().equals(user.getId())) {
            throw new AccessDeniedException("You can cancel only your own orders");
        }

        //  Asks Payment Service was payment ever made
        boolean paymentExists = paymentClient.isPaymentDone(orderId);

        // PAID ORDER PATH
        if (paymentExists) {

            //  Checks if refund already happened
            boolean refundAlreadyDone =
                    paymentClient.isRefundAlreadyProcessed(orderId);

            if (refundAlreadyDone) {
                return OrderCancelResponse.builder()
                        .id(order.getId())
                        .orderStatus(OrderStatus.CANCELLED.name())
                        .refundStatus("ALREADY_REFUNDED")
                        .build();
            }

            //  Refund internally
            PaymentResponse refundResponse =
                    paymentClient.refundPayment(orderId, authHeader);

            //  Cancel order + restore stock
            orderDomainService.cancelOrder(order);

            productStockService.restoreStock(
                    order.getProductId(),
                    order.getQuantity(),
                    authHeader
            );

            //  Respond to USER with refund info
            return OrderCancelResponse.builder()
                    .id(order.getId())
                    .orderStatus(OrderStatus.CANCELLED.name())
                    .refundStatus("REFUNDED")
                    .refundedAmount(refundResponse.getAmountPaid())
                    .build();
        }

        //  UNPAID ORDER
        orderDomainService.cancelOrder(order);

        productStockService.restoreStock(
                order.getProductId(),
                order.getQuantity(),
                authHeader
        );

        return OrderCancelResponse.builder()
                .id(order.getId())
                .orderStatus(OrderStatus.CANCELLED.name())
                .refundStatus("NOT_APPLICABLE")
                .build();
    }



    // ORDER HISTORY

    @Override
    public List<OrderHistoryResponse> getOrderHistory() {

        UserResponse user =
                userResolverService.resolveCurrentUser(null);

        return orderDomainService.getOrdersByUser(user.getId())
                .stream()
                .map(order -> OrderHistoryResponse.builder()
                        .orderId(order.getId())
                        .productId(order.getProductId())
                        .quantity(order.getQuantity())
                        .totalPrice(order.getTotalPrice())
                        .status(order.getStatus().name())
                        .createdAt(order.getCreatedAt())
                        .build())
                .toList();
    }

    //  ADMIN

    @Override
    public List<AdminOrderResponse> getAllOrdersForAdmin() {

        return orderDomainService.getAllOrders()
                .stream()
                .map(order -> AdminOrderResponse.builder()
                        .orderId(order.getId())
                        .userId(order.getUserId())
                        .productId(order.getProductId())
                        .quantity(order.getQuantity())
                        .totalPrice(order.getTotalPrice())
                        .status(order.getStatus().name())
                        .createdAt(order.getCreatedAt())
                        .build())
                .toList();
    }
    @Override
    public void markOrderAsPaid(Long orderId) {
        Order order = orderDomainService.getOrder(orderId);
        orderDomainService.markOrderAsPaid(order);
    }

    @Override
    public void markOrderAsPaymentFailed(Long orderId) {
        Order order = orderDomainService.getOrder(orderId);
        orderDomainService.markOrderAsPaymentFailed(order);
    }
    @Override
    public OrderResponse getOrderById(Long orderId, String authHeader) {
        Order order = orderDomainService.getOrder(orderId);
        return mapToResponse(order);
    }



    //  MAPPER

    private OrderResponse mapToResponse(Order order) {

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .orderStatus(order.getStatus().name())
                .paymentStatus("PAYMENT_PENDING")
                .build();

    }
}
