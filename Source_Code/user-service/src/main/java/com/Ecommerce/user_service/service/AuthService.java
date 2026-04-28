package com.Ecommerce.user_service.service;

import com.Ecommerce.user_service.dto.request.LoginRequest;
import com.Ecommerce.user_service.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}

