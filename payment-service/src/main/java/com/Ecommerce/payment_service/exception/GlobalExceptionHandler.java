package com.Ecommerce.payment_service.exception;

import com.Ecommerce.payment_service.dto.response.ApiErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Unauthorized payment access
    @ExceptionHandler(UnauthorizedPaymentAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(
            UnauthorizedPaymentAccessException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.FORBIDDEN,
                "PAYMENT_ACCESS_DENIED",
                ex.getMessage(),
                request
        );
    }

    // Payment not found
    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePaymentNotFound(
            PaymentNotFoundException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.NOT_FOUND,
                "PAYMENT_NOT_FOUND",
                ex.getMessage(),
                request
        );
    }

    //  Refund already processed
    @ExceptionHandler(RefundAlreadyProcessedException.class)
    public ResponseEntity<ApiErrorResponse> handleRefundAlreadyProcessed(
            RefundAlreadyProcessedException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.CONFLICT,
                "REFUND_ALREADY_PROCESSED",
                ex.getMessage(),
                request
        );
    }

    //  JWT expired
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiErrorResponse> handleJwtExpired(
            ExpiredJwtException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.UNAUTHORIZED,
                "JWT_EXPIRED",
                "Your session has expired. Please login again.",
                request
        );
    }

    //  JWT invalid
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiErrorResponse> handleJwtInvalid(
            JwtException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.UNAUTHORIZED,
                "JWT_INVALID",
                "Invalid authentication token.",
                request
        );
    }

    //  Validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String msg = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return build(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                msg,
                request
        );
    }

    //  Wrong URL
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleWrongUrl(
            NoHandlerFoundException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.NOT_FOUND,
                "INVALID_URL",
                "Requested endpoint does not exist.",
                request
        );
    }

    //  FINAL SAFETY NET
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "Payment service failed. Please try again later.",
                request
        );
    }

    private ResponseEntity<ApiErrorResponse> build(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(status)
                .body(new ApiErrorResponse(
                        false,
                        code,
                        message,
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }
}

