package com.Ecommerce.order_service.controller;

import com.Ecommerce.order_service.dto.response.OrderInternalResponse;
import com.Ecommerce.order_service.entity.Order;
import com.Ecommerce.order_service.entity.OrderStatus;
import com.Ecommerce.order_service.security.JwtFilter;
import com.Ecommerce.order_service.service.OrderDomainService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderInternalController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderInternalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderDomainService orderDomainService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtFilter jwtFilter;

    @Test
    void getOrderInternal_returnsOrderDetails() throws Exception {

        Order order = Order.builder()
                .id(1L)
                .userId(10L)
                .totalPrice(BigDecimal.valueOf(500))
                .status(OrderStatus.ORDER_PLACED)
                .createdAt(LocalDateTime.now())
                .build();

        when(orderDomainService.getOrder(1L))
                .thenReturn(order);

        mockMvc.perform(get("/internal/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.totalPrice").value(500))
                .andExpect(jsonPath("$.status").value("ORDER_PLACED"));
    }
}
