package com.Ecommerce.order_service.exception;

import com.Ecommerce.order_service.dto.response.ApiErrorResponse;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void startingMethod() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/orders");
    }


    @Test
    void handleOrderNotFound() {
        OrderNotFoundException ex = new OrderNotFoundException("Order not found");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleOrderNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode()).isEqualTo("ORDER_NOT_FOUND");
    }


    @Test
    void handleIllegalState() {
        IllegalStateException ex = new IllegalStateException("Invalid state");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleIllegalState(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo("INVALID_ORDER_STATE");
    }





    @Test
    void handleJwtExpired() {
        io.jsonwebtoken.ExpiredJwtException ex =
                mock(io.jsonwebtoken.ExpiredJwtException.class);

        ResponseEntity<ApiErrorResponse> response =
                handler.handleJwtExpired(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getErrorCode()).isEqualTo("JWT_EXPIRED");
    }


    @Test
    void handleJwtInvalid() {
        io.jsonwebtoken.JwtException ex =
                new io.jsonwebtoken.JwtException("Invalid token");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleJwtInvalid(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getErrorCode()).isEqualTo("JWT_INVALID");
    }


    @Test
    void handleValidationError() {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "request");

        bindingResult.addError(
                new FieldError("request", "name", "Name is required")
        );

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiErrorResponse> response =
                handler.handleValidation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("Name is required");
    }



    @Test
    void handleWrongUrl() {
        org.springframework.web.servlet.NoHandlerFoundException ex =
                new org.springframework.web.servlet.NoHandlerFoundException(
                        "GET",
                        "/invalid-url",
                        null
                );

        ResponseEntity<ApiErrorResponse> response =
                handler.handleWrongUrl(ex, request);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(response.getBody().getErrorCode())
                .isEqualTo("INVALID_URL");
    }




    @Test
    void handleGenericException() {
        Exception ex = new Exception("Boom");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleGeneric(ex, request);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getErrorCode()).isEqualTo("INTERNAL_ERROR");
    }
}
