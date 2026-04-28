package com.Ecommerce.order_service.controller;

import com.Ecommerce.order_service.dto.request.CreateOrderRequest;
import com.Ecommerce.order_service.dto.response.*;
import com.Ecommerce.order_service.security.JwtFilter;
import com.Ecommerce.order_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JwtFilter jwtFilter;



    // 6 test cases

    @Test
    void createOrder_returnsCreatedOrder() throws Exception {

        CreateOrderRequest request = new CreateOrderRequest(
                "product-1",
                2
        );

        OrderResponse response = OrderResponse.builder()
                .id(1L)
                .userId(10L)
                .productId("product-1")
                .quantity(2)
                .totalPrice(BigDecimal.valueOf(200))
                .orderStatus("ORDER_PLACED")
                .paymentStatus("PAYMENT_PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        when(orderService.createOrder(any(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.productId").value("product-1"));
    }



    @Test
    void getOrderHistory_returnsOrders() throws Exception {

        OrderHistoryResponse history = OrderHistoryResponse.builder()
                .orderId(1L)
                .productId("product-1")
                .quantity(1)
                .totalPrice(BigDecimal.valueOf(100))
                .status("ORDER_PLACED")
                .createdAt(LocalDateTime.now())
                .build();

        when(orderService.getOrderHistory())
                .thenReturn(List.of(history));

        mockMvc.perform(get("/api/orders/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1L));
    }

    @Test
    void getAllOrdersForAdmin_returnsOrders() throws Exception {

        AdminOrderResponse adminOrder = AdminOrderResponse.builder()
                .orderId(1L)
                .userId(10L)
                .productId("product-1")
                .quantity(2)
                .totalPrice(BigDecimal.valueOf(200))
                .status("ORDER_PLACED")
                .createdAt(LocalDateTime.now())
                .build();

        when(orderService.getAllOrdersForAdmin())
                .thenReturn(List.of(adminOrder));

        mockMvc.perform(get("/api/orders/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1L));
    }

    @Test
    void markOrderAsPaid_returnsOk() throws Exception {

        mockMvc.perform(put("/api/orders/internal/orders/1/paid"))
                .andExpect(status().isOk());
    }

    @Test
    void markOrderAsPaymentFailed_returnsOk() throws Exception {

        mockMvc.perform(put("/api/orders/internal/orders/1/payment-failed"))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderByIdInternal_returnsOrder() throws Exception {

        OrderResponse response = OrderResponse.builder()
                .id(1L)
                .userId(10L)
                .productId("product-1")
                .quantity(2)
                .totalPrice(BigDecimal.valueOf(200))
                .orderStatus("ORDER_PLACED")
                .paymentStatus("PAYMENT_PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        when(orderService.getOrderById(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(get("/api/orders/internal/orders/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1));

    }
}
