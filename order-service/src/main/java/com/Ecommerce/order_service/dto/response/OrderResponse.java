package com.Ecommerce.order_service.dto.response;

import com.Ecommerce.order_service.entity.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponse {

    @JsonProperty("orderId")
    private Long id;

    private Long userId;
    private String productId;
    private Integer quantity;

    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private String orderStatus;
    private String paymentStatus;
}

