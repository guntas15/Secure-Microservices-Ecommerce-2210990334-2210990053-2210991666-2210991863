package com.Ecommerce.user_service.service;

import com.Ecommerce.user_service.dto.response.UserResponse;

import java.util.List;

public interface UserQueryService {

    UserResponse getUserByEmail(String email);
    List<UserResponse> getAllUsers();
}

