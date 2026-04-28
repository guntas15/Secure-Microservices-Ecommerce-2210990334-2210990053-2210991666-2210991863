package com.Ecommerce.user_service.service.impl;

import com.Ecommerce.user_service.dto.request.LoginRequest;
import com.Ecommerce.user_service.dto.request.RegisterRequest;
import com.Ecommerce.user_service.dto.response.LoginResponse;
import com.Ecommerce.user_service.dto.response.UserResponse;
import com.Ecommerce.user_service.service.AuthService;
import com.Ecommerce.user_service.service.UserCommandService;
import com.Ecommerce.user_service.service.UserQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private UserCommandService userCommandService;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void login_shouldDirectToAuthService() {

        LoginRequest request = new LoginRequest(
                "user@test.com",
                "password"
        );

        LoginResponse loginResponse = LoginResponse.builder()
                .token("jwt-token")
                .build();

        when(authService.login(request))
                .thenReturn(loginResponse);

        LoginResponse response = userService.login(request);

        assertEquals("jwt-token", response.getToken());
        verify(authService).login(request);
    }

    @Test
    void register_shouldDirectToUserCommandService() {

        RegisterRequest request = new RegisterRequest(
                "new@test.com",
                "password"
        );

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .email("new@test.com")
                .role("USER")
                .build();

        when(userCommandService.register(request))
                .thenReturn(userResponse);

        UserResponse response = userService.register(request);

        assertEquals("new@test.com", response.getEmail());
        verify(userCommandService).register(request);
    }

    @Test
    void getUserByEmail_shouldDirectToUserQueryService() {

        String email = "user@test.com";

        UserResponse userResponse = UserResponse.builder()
                .id(2L)
                .email(email)
                .role("ADMIN")
                .build();

        when(userQueryService.getUserByEmail(email))
                .thenReturn(userResponse);

        UserResponse response = userService.getUserByEmail(email);

        assertEquals("ADMIN", response.getRole());
        verify(userQueryService).getUserByEmail(email);
    }

    @Test
    void getAllUsers_shouldDirectToUserQueryService() {

        UserResponse u1 = UserResponse.builder()
                .id(1L)
                .email("a@test.com")
                .role("USER")
                .build();

        UserResponse u2 = UserResponse.builder()
                .id(2L)
                .email("b@test.com")
                .role("ADMIN")
                .build();

        when(userQueryService.getAllUsers())
                .thenReturn(List.of(u1, u2));

        List<UserResponse> responses = userService.getAllUsers();

        assertEquals(2, responses.size());
        verify(userQueryService).getAllUsers();
    }





}

