package com.Ecommerce.user_service.controller;

import com.Ecommerce.user_service.dto.request.LoginRequest;
import com.Ecommerce.user_service.dto.request.RegisterRequest;
import com.Ecommerce.user_service.dto.response.LoginResponse;
import com.Ecommerce.user_service.dto.response.UserResponse;
import com.Ecommerce.user_service.exception.GlobalExceptionHandler;
import com.Ecommerce.user_service.security.JwtAuthenticationFilter;
import com.Ecommerce.user_service.security.JwtUtil;
import com.Ecommerce.user_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // REQUIRED for context to load
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void register_returnsCreatedUser() throws Exception {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .email("test@test.com")
                .role("USER")
                .build();

        when(userService.register(any(RegisterRequest.class)))
                .thenReturn(userResponse);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@test.com",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("User registered successfully"))
                .andExpect(jsonPath("$.data.email")
                        .value("test@test.com"));
    }

    @Test
    void register_fails_whenInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_returnsLoginResponse() throws Exception {
        LoginResponse loginResponse = LoginResponse.builder()
                .token("jwt-token")
                .build();

        when(userService.login(any(LoginRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@test.com",
                                  "password": "password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Login successful"))
                .andExpect(jsonPath("$.data.token")
                        .value("jwt-token"));
    }

    @Test
    void login_fails_whenInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserByEmail_returnsUser() throws Exception {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .email("test@test.com")
                .role("USER")
                .build();

        when(userService.getUserByEmail("test@test.com"))
                .thenReturn(userResponse);

        mockMvc.perform(get("/api/users/by-email")
                        .param("email", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email")
                        .value("test@test.com"));
    }
}

