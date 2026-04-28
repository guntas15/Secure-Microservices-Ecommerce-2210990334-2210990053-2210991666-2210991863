package com.Ecommerce.order_service.dto.request;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class PaymentRequest {
    private Long orderId;
    private String paymentMode;
}



