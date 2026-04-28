package com.Ecommerce.payment_service.service.impl;

import com.Ecommerce.payment_service.client.OrderClient;
import com.Ecommerce.payment_service.dto.request.PaymentRequest;
import com.Ecommerce.payment_service.dto.response.OrderResponse;
import com.Ecommerce.payment_service.dto.response.PaymentResponse;
import com.Ecommerce.payment_service.entity.Payment;
import com.Ecommerce.payment_service.entity.PaymentStatus;
import com.Ecommerce.payment_service.entity.PaymentType;
import com.Ecommerce.payment_service.exception.PaymentNotFoundException;
import com.Ecommerce.payment_service.exception.RefundAlreadyProcessedException;
import com.Ecommerce.payment_service.exception.UnauthorizedPaymentAccessException;
import com.Ecommerce.payment_service.kafka.producer.PaymentEventProducer;
import com.Ecommerce.payment_service.repository.PaymentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderClient orderClient;

    @Mock
    private PaymentEventProducer paymentEventProducer;

    // ===================== isPaymentDone =====================

    @Test
    void isPaymentDone_returnsTrue_whenPaymentExists() {
        when(paymentRepository.findByOrderIdAndType(1L, PaymentType.PAYMENT))
                .thenReturn(Optional.of(new Payment()));

        boolean result = paymentService.isPaymentDone(1L);

        assertThat(result).isTrue();
    }
    @Test
    void isPaymentDone_returnsFalse_whenPaymentDoesNotExist() {

        when(paymentRepository.findByOrderIdAndType(1L, PaymentType.PAYMENT))
                .thenReturn(Optional.empty());

        boolean result = paymentService.isPaymentDone(1L);

        assertThat(result).isFalse();
    }


    @Test
    void isPaymentDone_returnsFalse_whenPaymentMissing() {
        when(paymentRepository.findByOrderIdAndType(1L, PaymentType.PAYMENT))
                .thenReturn(Optional.empty());

        boolean result = paymentService.isPaymentDone(1L);

        assertThat(result).isFalse();
    }

    // ===================== isRefundAlreadyProcessed =====================

    @Test
    void isRefundAlreadyProcessed_returnsTrue() {
        when(paymentRepository.findByOrderIdAndType(1L, PaymentType.REFUND))
                .thenReturn(Optional.of(new Payment()));

        assertThat(paymentService.isRefundAlreadyProcessed(1L)).isTrue();
    }

    @Test
    void isRefundAlreadyProcessed_returnsFalse() {
        when(paymentRepository.findByOrderIdAndType(1L, PaymentType.REFUND))
                .thenReturn(Optional.empty());

        assertThat(paymentService.isRefundAlreadyProcessed(1L)).isFalse();
    }

    // ===================== getPaymentDetailsByOrderId =====================

    @Test
    void getPaymentDetailsByOrderId_returnsPayment() {
        Payment payment = Payment.builder()
                .id(1L)
                .orderId(10L)
                .status(PaymentStatus.SUCCESS)
                .amount(BigDecimal.valueOf(100))
                .paymentMode("CARD")
                .build();

        when(paymentRepository.findByOrderId(10L))
                .thenReturn(Optional.of(payment));

        PaymentResponse response =
                paymentService.getPaymentDetailsByOrderId(10L);

        assertThat(response.getOrderId()).isEqualTo(10L);
        assertThat(response.getPaymentStatus()).isEqualTo("SUCCESS");
        assertThat(response.getAmountPaid()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void getPaymentDetailsByOrderId_throwsException_whenNotFound() {
        when(paymentRepository.findByOrderId(10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                paymentService.getPaymentDetailsByOrderId(10L))
                .isInstanceOf(PaymentNotFoundException.class);
    }



    @Test
    void getPaymentForUser_returnsPayment_whenAuthorized() {
        OrderResponse order =
                new OrderResponse(10L, 5L, BigDecimal.valueOf(200), "ORDER_PLACED");

        Payment payment = Payment.builder()
                .id(1L)
                .orderId(10L)
                .status(PaymentStatus.SUCCESS)
                .amount(BigDecimal.valueOf(200))
                .paymentMode("UPI")
                .build();

        when(orderClient.getOrderById(10L, "token"))
                .thenReturn(order);

        when(paymentRepository.findByOrderId(10L))
                .thenReturn(Optional.of(payment));

        PaymentResponse response =
                paymentService.getPaymentForUser(10L, 5L, "token");

        assertThat(response.getPaymentStatus()).isEqualTo("SUCCESS");
        assertThat(response.getOrderId()).isEqualTo(10L);
    }


    @Test
    void getPaymentForUser_throwsException_whenNotFound() {

        OrderResponse order = new OrderResponse(10L, 5L, BigDecimal.valueOf(200), "ORDER_PLACED");

        when( orderClient.getOrderById(anyLong(), anyString()))
                .thenReturn(order);
        when(paymentRepository.findByOrderId(anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(PaymentNotFoundException.class, () -> paymentService.getPaymentForUser(10L, 5L, "token"));

    }

@Test
void getPaymentForUser_throwsException_whenPaymentNotFound() {
        OrderResponse  order = new OrderResponse(10L,5L,BigDecimal.valueOf(200), "ORDER_PLACED");

    when(orderClient.getOrderById(anyLong(), anyString()))
            .thenReturn(order);

    Assertions.assertThrows(PaymentNotFoundException.class, () -> paymentService.getPaymentForUser(1L, 22L, "bearer token"));



}

    @Test
    void getPaymentForUser_throwsException_whenUserMismatch() {
        OrderResponse order =
                new OrderResponse(10L, 99L, BigDecimal.valueOf(200), "ORDER_PLACED");

        when(orderClient.getOrderById(10L, "token"))
                .thenReturn(order);

        assertThatThrownBy(() ->
                paymentService.getPaymentForUser(10L, 5L, "token"))
                .isInstanceOf(UnauthorizedPaymentAccessException.class);
    }
    @Test
    void refundPayment_throwsException_PaymentNotfound() {

        when(paymentRepository.findByOrderIdAndType(anyLong(),eq(PaymentType.PAYMENT)))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() ->
                paymentService.refundPayment(10L, "token"))
                .isInstanceOf(PaymentNotFoundException.class);


    }



    @Test
    void refundPayment_createsRefundSuccessfully() {
        Payment originalPayment = Payment.builder()
                .id(1L)
                .orderId(10L)
                .amount(BigDecimal.valueOf(100))
                .paymentMode("CARD")
                .type(PaymentType.PAYMENT)
                .status(PaymentStatus.SUCCESS)
                .build();

        when(paymentRepository.findByOrderIdAndType(10L, PaymentType.PAYMENT))
                .thenReturn(Optional.of(originalPayment));

        when(paymentRepository.findByOrderIdAndType(10L, PaymentType.REFUND))
                .thenReturn(Optional.empty());

        PaymentResponse response =
                paymentService.refundPayment(10L, "token");

        assertThat(response.getPaymentStatus()).isEqualTo("REFUNDED");
        assertThat(response.getAmountPaid()).isEqualTo(BigDecimal.valueOf(100));

        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void refundPayment_throwsException_whenAlreadyRefunded() {
        Payment originalPayment = Payment.builder()
                .orderId(10L)
                .type(PaymentType.PAYMENT)
                .status(PaymentStatus.SUCCESS)
                .build();

        when(paymentRepository.findByOrderIdAndType(10L, PaymentType.PAYMENT))
                .thenReturn(Optional.of(originalPayment));

        when(paymentRepository.findByOrderIdAndType(10L, PaymentType.REFUND))
                .thenReturn(Optional.of(new Payment()));

        assertThatThrownBy(() ->
                paymentService.refundPayment(10L, "token"))
                .isInstanceOf(RefundAlreadyProcessedException.class);
    }

    // ===================== getAllPaymentsForAdmin =====================

    @Test
    void getAllPaymentsForAdmin_returnsList() {
        Payment p1 = Payment.builder()
                .id(1L)
                .orderId(10L)
                .status(PaymentStatus.SUCCESS)
                .amount(BigDecimal.valueOf(100))
                .paymentMode("CARD")
                .build();

        Payment p2 = Payment.builder()
                .id(2L)
                .orderId(11L)
                .status(PaymentStatus.FAILED)
                .amount(BigDecimal.valueOf(50))
                .paymentMode("UPI")
                .build();

        when(paymentRepository.findAll())
                .thenReturn(List.of(p1, p2));

        List<PaymentResponse> list =
                paymentService.getAllPaymentsForAdmin();

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getPaymentStatus()).isEqualTo("SUCCESS");
        assertThat(list.get(1).getPaymentStatus()).isEqualTo("FAILED");
    }

    // ===================== doPayment (CRITICAL FOR 90%+) =====================

    @Test
    void doPayment_returnsAlreadyPaid_whenPaymentAlreadySuccessful() {

        Payment existing = Payment.builder()
                .id(1L)
                .orderId(10L)
                .status(PaymentStatus.SUCCESS)
                .amount(BigDecimal.valueOf(300))
                .paymentMode("CARD")
                .type(PaymentType.PAYMENT)
                .build();

        when(paymentRepository.findByOrderIdAndType(10L, PaymentType.PAYMENT))
                .thenReturn(Optional.of(existing));

        PaymentRequest request =
                new PaymentRequest(10L, "CARD");

        PaymentResponse response =
                paymentService.doPayment(request, "token");

        assertThat(response.getPaymentStatus()).isEqualTo("ALREADY_PAID");

        verifyNoInteractions(orderClient);
        verifyNoInteractions(paymentEventProducer);
    }

    @Test
    void doPayment_successfulPayment_publishesCompletedEvent() {

        when(paymentRepository.findByOrderIdAndType(10L, PaymentType.PAYMENT))
                .thenReturn(Optional.empty());

        OrderResponse order =
                new OrderResponse(10L, 5L, BigDecimal.valueOf(800), "ORDER_PLACED");

        when(orderClient.getOrderById(10L, "token"))
                .thenReturn(order);

        PaymentRequest request =
                new PaymentRequest(10L, "UPI");

        PaymentResponse response =
                paymentService.doPayment(request, "token");

        assertThat(response.getPaymentStatus()).isEqualTo("SUCCESS");

        verify(paymentRepository).save(any(Payment.class));
        verify(paymentEventProducer).publishPaymentCompleted(any());
        verify(paymentEventProducer, never()).publishPaymentFailed(any());
    }

    @Test
    void doPayment_failedPayment_publishesFailedEvent() {

        when(paymentRepository.findByOrderIdAndType(10L, PaymentType.PAYMENT))
                .thenReturn(Optional.empty());

        OrderResponse order =
                new OrderResponse(10L, 5L, BigDecimal.valueOf(200), "ORDER_PLACED");

        when(orderClient.getOrderById(10L, "token"))
                .thenReturn(order);

        PaymentRequest request =
                new PaymentRequest(10L, "CARD");

        PaymentResponse response =
                paymentService.doPayment(request, "token");

        assertThat(response.getPaymentStatus()).isEqualTo("FAILED");

        verify(paymentRepository).save(any(Payment.class));
        verify(paymentEventProducer).publishPaymentFailed(any());
        verify(paymentEventProducer, never()).publishPaymentCompleted(any());
    }

    @Test
    void doPayment_amountExactly500_isSuccessful() {

        when(paymentRepository.findByOrderIdAndType(10L, PaymentType.PAYMENT))
                .thenReturn(Optional.empty());

        OrderResponse order =
                new OrderResponse(10L, 5L, BigDecimal.valueOf(500), "ORDER_PLACED");

        when(orderClient.getOrderById(10L, "token"))
                .thenReturn(order);

        PaymentRequest request =
                new PaymentRequest(10L, "UPI");

        PaymentResponse response =
                paymentService.doPayment(request, "token");

        assertThat(response.getPaymentStatus()).isEqualTo("SUCCESS");
    }
}
