package com.Ecommerce.order_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentResponse {
    private Long paymentId;
    private Long orderId;

    private String paymentStatus;
    private BigDecimal amountPaid;
    private String paymentMode;
}

