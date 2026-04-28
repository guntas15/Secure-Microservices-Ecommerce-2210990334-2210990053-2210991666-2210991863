package com.Ecommerce.user_service.exception;

import com.Ecommerce.user_service.dto.response.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
        objectMapper = new ObjectMapper();
    }


    @Test
    void handleUserAlreadyExists() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/users/register");

        var response = handler.handleUserExists(
                new UserAlreadyExistsException("User already exists"),
                request
        );

        ApiErrorResponse body = response.getBody();

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(body.getErrorCode()).isEqualTo("USER_ALREADY_EXISTS");
    }


    @Test
    void handleInvalidCredentials() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/users/login");

        var response = handler.handleInvalidCredentials(
                new InvalidCredentialsException("Invalid login"),
                request
        );

        ApiErrorResponse body = response.getBody();

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(body.getErrorCode()).isEqualTo("INVALID_CREDENTIALS");
    }


    @Test
    void handleJwtExpired() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        var response = handler.handleJwtExpired(
                new ExpiredJwtException(null, null, "Expired"),
                request
        );

        ApiErrorResponse body = response.getBody();

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(body.getErrorCode()).isEqualTo("JWT_EXPIRED");
    }


    @Test
    void handleJwtInvalid() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        var response = handler.handleJwtInvalid(
                new JwtException("Invalid token"),
                request
        );

        ApiErrorResponse body = response.getBody();

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(body.getErrorCode()).isEqualTo("JWT_INVALID");
    }


    @Test
    void handleGenericException() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        var response = handler.handleGeneric(
                new RuntimeException("Boom"),
                request
        );

        ApiErrorResponse body = response.getBody();

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(body.getErrorCode()).isEqualTo("INTERNAL_ERROR");
    }
}
