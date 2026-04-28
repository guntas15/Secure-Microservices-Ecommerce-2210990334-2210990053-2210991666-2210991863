package com.Ecommerce.payment_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentRequest {
    private Long orderId;
    private String paymentMode;
}
