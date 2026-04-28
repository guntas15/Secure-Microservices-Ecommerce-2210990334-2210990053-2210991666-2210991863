package com.Ecommerce.user_service.service;

import com.Ecommerce.user_service.dto.request.LoginRequest;
import com.Ecommerce.user_service.dto.request.RegisterRequest;
import com.Ecommerce.user_service.dto.response.LoginResponse;
import com.Ecommerce.user_service.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    LoginResponse login(LoginRequest request);

    UserResponse register(RegisterRequest request);
    UserResponse getUserByEmail(String email);
    List<UserResponse> getAllUsers();

}
