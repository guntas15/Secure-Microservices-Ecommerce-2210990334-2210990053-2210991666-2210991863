package com.Ecommerce.user_service.service;

import com.Ecommerce.user_service.dto.request.RegisterRequest;
import com.Ecommerce.user_service.dto.response.UserResponse;

public interface UserCommandService {

    UserResponse register(RegisterRequest request);
}

