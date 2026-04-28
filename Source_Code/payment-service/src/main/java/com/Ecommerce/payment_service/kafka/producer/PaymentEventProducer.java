package com.Ecommerce.payment_service.kafka.producer;

import com.Ecommerce.payment_service.kafka.event.PaymentCompletedEvent;
import com.Ecommerce.payment_service.kafka.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String PAYMENT_TOPIC = "payment-events";

    public void publishPaymentCompleted(PaymentCompletedEvent event){
        kafkaTemplate.send(PAYMENT_TOPIC, String.valueOf(event.getOrderId()),event);
    }

    public void publishPaymentFailed(PaymentFailedEvent event){
        kafkaTemplate.send(PAYMENT_TOPIC, String.valueOf(event.getOrderId()),event);
    }

}
