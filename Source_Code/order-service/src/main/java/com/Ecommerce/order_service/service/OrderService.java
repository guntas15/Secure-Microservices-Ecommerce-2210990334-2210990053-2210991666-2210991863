package com.Ecommerce.order_service.service;

import com.Ecommerce.order_service.dto.request.CreateOrderRequest;
import com.Ecommerce.order_service.dto.response.AdminOrderResponse;
import com.Ecommerce.order_service.dto.response.OrderCancelResponse;
import com.Ecommerce.order_service.dto.response.OrderHistoryResponse;
import com.Ecommerce.order_service.dto.response.OrderResponse;

import java.util.List;



public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request, String authHeader);

    OrderCancelResponse cancelOrder(Long orderId, String authHeader);

    List<OrderHistoryResponse> getOrderHistory();

    List<AdminOrderResponse> getAllOrdersForAdmin();

    OrderResponse getOrderById(Long orderId, String authHeader);


    void markOrderAsPaid(Long orderId);

    void markOrderAsPaymentFailed(Long orderId);



}

