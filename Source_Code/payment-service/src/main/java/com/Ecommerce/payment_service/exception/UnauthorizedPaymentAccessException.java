package com.Ecommerce.payment_service.exception;



public class UnauthorizedPaymentAccessException extends RuntimeException {

    public UnauthorizedPaymentAccessException(String message) {
        super(message);
    }
}

