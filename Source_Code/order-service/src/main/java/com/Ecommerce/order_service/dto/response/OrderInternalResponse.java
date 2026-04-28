package com.Ecommerce.order_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderInternalResponse {
    private Long id;
    private Long userId;
    private BigDecimal totalPrice;
    private String status;
}

