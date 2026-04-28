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
import com.Ecommerce.payment_service.kafka.event.PaymentCompletedEvent;
import com.Ecommerce.payment_service.kafka.event.PaymentFailedEvent;
import com.Ecommerce.payment_service.kafka.producer.PaymentEventProducer;
import com.Ecommerce.payment_service.repository.PaymentRepository;
import com.Ecommerce.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;
    private final PaymentEventProducer paymentEventProducer;


    @Override
    public PaymentResponse doPayment(PaymentRequest request, String authHeader) {
        // CHECKS IF PAYMENT ALREADY PAID
        Optional<Payment> existingPayment =
                paymentRepository.findByOrderIdAndType(
                        request.getOrderId(),
                        PaymentType.PAYMENT
                );

        if (existingPayment.isPresent()
                && existingPayment.get().getStatus() == PaymentStatus.SUCCESS) {

            Payment p = existingPayment.get();

            return PaymentResponse.builder()
                    .paymentId(p.getId())
                    .orderId(p.getOrderId())
                    .paymentStatus("ALREADY_PAID")
                    .amountPaid(p.getAmount())
                    .paymentMode(p.getPaymentMode())
                    .build();
        }

        // 1. Fetch order from Order Service
        OrderResponse order =
                orderClient.getOrderById(
                        request.getOrderId(),
                        authHeader
                );

        // 2. Get amount ONLY from order service
        BigDecimal amount = order.getTotalPrice();

        // 3. Simulate payment logic
        boolean success =
                amount.compareTo(BigDecimal.valueOf(500)) >= 0;


        Payment payment = Payment.builder()
                .orderId(order.getId())
                .amount(amount)
                .paymentMode(request.getPaymentMode())
                .type(PaymentType.PAYMENT)
                .status(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .build();

        paymentRepository.save(payment);

        if (payment.getStatus() == PaymentStatus.SUCCESS) {

            paymentEventProducer.publishPaymentCompleted(
                    new PaymentCompletedEvent(
                            payment.getOrderId(),
                            payment.getAmount(),
                            payment.getPaymentMode()
                    )
            );

        } else {

            paymentEventProducer.publishPaymentFailed(
                    new PaymentFailedEvent(
                            payment.getOrderId(),
                            "PAYMENT_FAILED"
                    )
            );
        }



        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .paymentStatus(payment.getStatus().name())
                .amountPaid(payment.getAmount())
                .paymentMode(payment.getPaymentMode())
                .build();

    }
    @Override
    public boolean isPaymentDone(Long orderId) {
        return paymentRepository
                .findByOrderIdAndType(orderId, PaymentType.PAYMENT)
                .isPresent();
    }

    @Override
    public PaymentResponse refundPayment(Long orderId, String authHeader) {
        // ORIGINAL PAYMENT VERIFICATION
        Payment originalPayment=
                paymentRepository.findByOrderIdAndType(orderId,PaymentType.PAYMENT)
                        .orElseThrow(() -> new PaymentNotFoundException("Payment not found")
                        );
        // DOUBLE REFUND PREVENTION
        paymentRepository.findByOrderIdAndType(orderId, PaymentType.REFUND)
                .ifPresent(p -> {
                    throw new RefundAlreadyProcessedException("Refund already processed");
                });

        // REFUND CREATION
        Payment refund = Payment.builder()
                .orderId(orderId)
                .amount(originalPayment.getAmount())
                .paymentMode(originalPayment.getPaymentMode())
                .type(PaymentType.REFUND)
                .status(PaymentStatus.SUCCESS)
                .build();
        paymentRepository.save(refund);

        return PaymentResponse.builder()
                .paymentId(refund.getId())
                .orderId(refund.getOrderId())
                .paymentStatus("REFUNDED")
                .amountPaid(refund.getAmount())
                .paymentMode(refund.getPaymentMode())
                .build();

    }
    @Override
    public boolean isRefundAlreadyProcessed(Long orderId) {
        return paymentRepository
                .findByOrderIdAndType(orderId, PaymentType.REFUND)
                .isPresent();
    }


    @Override
    public PaymentResponse getPaymentForUser(
            Long orderId,
            Long userId,
            String authHeader
    ) {

        OrderResponse order =
                orderClient.getOrderById(orderId, authHeader);

        if (!order.getUserId().equals(userId)) {
            throw new UnauthorizedPaymentAccessException(
                    "You are not allowed to access this payment"
            );
        }

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new PaymentNotFoundException("No payment found for order " + orderId)
                );


        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .paymentStatus(payment.getStatus().name())
                .amountPaid(payment.getAmount())
                .paymentMode(payment.getPaymentMode())
                .build();

    }

    @Override
    public List<PaymentResponse> getAllPaymentsForAdmin() {

        return paymentRepository.findAll()
                .stream()
                .map(p -> PaymentResponse.builder()
                        .paymentId(p.getId())
                        .orderId(p.getOrderId())
                        .paymentStatus(p.getStatus().name())
                        .amountPaid(p.getAmount())
                        .paymentMode(p.getPaymentMode())
                        .build())

                .toList();
    }
    @Override
    public PaymentResponse getPaymentDetailsByOrderId(Long orderId) {

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new PaymentNotFoundException("No payment found for order " + orderId)
                );


        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .paymentStatus(payment.getStatus().name())
                .amountPaid(payment.getAmount())
                .paymentMode(payment.getPaymentMode())
                .build();

    }


}



