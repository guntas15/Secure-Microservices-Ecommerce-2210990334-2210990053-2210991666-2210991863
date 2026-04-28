package com.Ecommerce.payment_service.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCompletedEvent {


    private long orderId;
    private BigDecimal  amountPaid;
    private String paymentType;
}
