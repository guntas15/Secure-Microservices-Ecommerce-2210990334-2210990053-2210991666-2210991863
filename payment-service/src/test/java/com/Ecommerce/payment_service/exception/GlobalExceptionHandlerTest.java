package com.Ecommerce.payment_service.exception;

import com.Ecommerce.payment_service.dto.response.ApiErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/test");
    }

    @Test
    void handleUnauthorizedPaymentAccess() {
        ResponseEntity<ApiErrorResponse> response =
                handler.handleUnauthorized(
                        new UnauthorizedPaymentAccessException("Not allowed"),
                        request
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getErrorCode())
                .isEqualTo("PAYMENT_ACCESS_DENIED");
    }

    @Test
    void handlePaymentNotFound() {
        ResponseEntity<ApiErrorResponse> response =
                handler.handlePaymentNotFound(
                        new PaymentNotFoundException("Not found"),
                        request
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode())
                .isEqualTo("PAYMENT_NOT_FOUND");
    }

    @Test
    void handleRefundAlreadyProcessed() {
        ResponseEntity<ApiErrorResponse> response =
                handler.handleRefundAlreadyProcessed(
                        new RefundAlreadyProcessedException("Refunded"),
                        request
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getErrorCode())
                .isEqualTo("REFUND_ALREADY_PROCESSED");
    }

    @Test
    void handleJwtExpired() {
        ResponseEntity<ApiErrorResponse> response =
                handler.handleJwtExpired(
                        new ExpiredJwtException(null, null, "Expired"),
                        request
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getErrorCode())
                .isEqualTo("JWT_EXPIRED");
    }

    @Test
    void handleJwtInvalid() {
        ResponseEntity<ApiErrorResponse> response =
                handler.handleJwtInvalid(
                        new JwtException("Invalid"),
                        request
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getErrorCode())
                .isEqualTo("JWT_INVALID");
    }


    @Test
    void handleValidationError() {

        // Create a fake BindingResult with one validation error
        BindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "request");

        bindingResult.addError(
                new FieldError(
                        "request",
                        "field",
                        "Field is required"
                )
        );

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiErrorResponse> response =
                handler.handleValidation(ex, request);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(response.getBody().getErrorCode())
                .isEqualTo("VALIDATION_ERROR");

        assertThat(response.getBody().getMessage())
                .isEqualTo("Field is required");
    }


    @Test
    void handleWrongUrl() {
        ResponseEntity<ApiErrorResponse> response =
                handler.handleWrongUrl(
                        new NoHandlerFoundException(
                                "GET",
                                "/invalid",
                                null
                        ),
                        request
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode())
                .isEqualTo("INVALID_URL");
    }

    @Test
    void handleGenericException() {
        ResponseEntity<ApiErrorResponse> response =
                handler.handleGeneric(
                        new RuntimeException("Boom"),
                        request
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getErrorCode())
                .isEqualTo("INTERNAL_ERROR");
    }
}
