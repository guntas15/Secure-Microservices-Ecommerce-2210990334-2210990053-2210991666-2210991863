package com.Ecommerce.product_service.exception;

import com.Ecommerce.product_service.dto.response.ApiErrorResponse;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();

        mockMvc = MockMvcBuilders
                .standaloneSetup()
                .setControllerAdvice(handler)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void handleProductNotFoundException() {
        MockHttpServletRequest request =
                new MockHttpServletRequest("GET", "/products/1");

        var response = handler.handleNotFound(
                new ProductNotFoundException("1"),
                request
        );

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody().getErrorCode()).isEqualTo("PRODUCT_NOT_FOUND");
        assertThat(response.getBody().getMessage())
                .isEqualTo("Product not found with id: 1");
        assertThat(response.getBody().getPath()).isEqualTo("/products/1");
    }


    @Test
    void handleInsufficientStockException() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/products/reduce");

        var response = handler.handleInsufficientStock(
                new InsufficientStockException("Stock not available"),
                request
        );

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getErrorCode()).isEqualTo("INSUFFICIENT_STOCK");
    }

    @Test
    void handleOptimisticLockException() {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/products/1");

        var response = handler.handleOptimisticLock(
                new OptimisticLockException(),
                request
        );

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody().getErrorCode()).isEqualTo("CONCURRENT_UPDATE");
    }

    @Test
    void handleValidationException() {
        BindException bindException = new BindException(new Object(), "product");


        bindException.addError(
                new FieldError("product", "name", "Validation failed")
        );


        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindException);

        MockHttpServletRequest request =
                new MockHttpServletRequest("POST", "/products");

        var response = handler.handleValidation(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed");
    }

    @Test
    void handleNoHandlerFoundException() {
        MockHttpServletRequest request =
                new MockHttpServletRequest("GET", "/wrong-url");

        var response = handler.handleWrongUrl(
                new NoHandlerFoundException("GET", "/wrong-url", null),
                request
        );

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody().getErrorCode()).isEqualTo("INVALID_URL");
    }

    @Test
    void handleGenericException() {
        MockHttpServletRequest request =
                new MockHttpServletRequest("GET", "/any");

        var response = handler.handleGeneric(
                new RuntimeException("Boom"),
                request
        );

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().getErrorCode()).isEqualTo("INTERNAL_ERROR");
    }
}
