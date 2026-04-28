package com.Ecommerce.order_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderCancelResponse {

    @JsonProperty("orderId")
    private Long id;

    private String orderStatus;
    private String refundStatus;
    private BigDecimal refundedAmount;
}
