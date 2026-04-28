package com.Ecommerce.payment_service.controller;

import com.Ecommerce.payment_service.dto.request.PaymentRequest;
import com.Ecommerce.payment_service.dto.response.PaymentResponse;
import com.Ecommerce.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> doPayment(
            @PathVariable Long orderId,
            @RequestBody PaymentRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        request.setOrderId(orderId); //
        return ResponseEntity.ok(
                paymentService.doPayment(request, authHeader)
        );
    }


    @GetMapping("/{orderId}/payment-exists")
    public ResponseEntity<Boolean> isPaymentDone(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                paymentService.isPaymentDone(orderId)
        );
    }



    @PostMapping("/internal/refund/{orderId}")
    public ResponseEntity<PaymentResponse> refundPaymentInternal(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(
                paymentService.refundPayment(orderId, authHeader)
        );
    }

    @GetMapping("/{orderId}/refund-exists")
    public ResponseEntity<Boolean> isRefundAlreadyProcessed(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                paymentService.isRefundAlreadyProcessed(orderId)
        );
    }



    // INTERNAL: called by Order Service only
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(
                paymentService.getPaymentDetailsByOrderId(orderId)
        );
    }


    // ADMIN API (still protected by Spring Security)
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {

        return ResponseEntity.ok(
                paymentService.getAllPaymentsForAdmin()
        );
    }
}


