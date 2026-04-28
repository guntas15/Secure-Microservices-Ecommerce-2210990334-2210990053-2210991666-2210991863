package com.Ecommerce.user_service.service.impl;

import com.Ecommerce.user_service.dto.request.LoginRequest;
import com.Ecommerce.user_service.dto.request.RegisterRequest;
import com.Ecommerce.user_service.dto.response.LoginResponse;
import com.Ecommerce.user_service.dto.response.UserResponse;
import com.Ecommerce.user_service.service.AuthService;
import com.Ecommerce.user_service.service.UserCommandService;
import com.Ecommerce.user_service.service.UserQueryService;
import com.Ecommerce.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthService authService;
    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    @Override
    public LoginResponse login(LoginRequest request) {
        return authService.login(request);
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        return userCommandService.register(request);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        return userQueryService.getUserByEmail(email);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userQueryService.getAllUsers();
    }

}
