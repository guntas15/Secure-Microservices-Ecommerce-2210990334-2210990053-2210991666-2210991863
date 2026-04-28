package com.Ecommerce.payment_service.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RefundAlreadyProcessedException extends RuntimeException {
    public RefundAlreadyProcessedException(String message) {
        super(message);
    }
}


