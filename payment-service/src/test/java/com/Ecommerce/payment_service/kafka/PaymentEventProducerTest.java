package com.Ecommerce.payment_service.kafka;

import com.Ecommerce.payment_service.kafka.event.PaymentCompletedEvent;
import com.Ecommerce.payment_service.kafka.event.PaymentFailedEvent;
import com.Ecommerce.payment_service.kafka.producer.PaymentEventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private PaymentEventProducer paymentEventProducer;

    @Test
    void shouldPublishPaymentCompletedEvent() {

        // Arrange
        PaymentCompletedEvent event =
                new PaymentCompletedEvent(
                        1L,
                        BigDecimal.valueOf(1000),
                        "CARD"
                );

        // Act
        paymentEventProducer.publishPaymentCompleted(event);

        // Assert
        verify(kafkaTemplate, times(1))
                .send(
                        eq("payment-events"),
                        eq("1"),
                        eq(event)
                );
    }

    @Test
    void shouldPublishPaymentFailedEvent() {

        // Arrange
        PaymentFailedEvent event =
                new PaymentFailedEvent(
                        2L,
                        "INSUFFICIENT_BALANCE"
                );

        // Act
        paymentEventProducer.publishPaymentFailed(event);

        // Assert
        verify(kafkaTemplate, times(1))
                .send(
                        eq("payment-events"),
                        eq("2"),
                        eq(event)
                );
    }
}
