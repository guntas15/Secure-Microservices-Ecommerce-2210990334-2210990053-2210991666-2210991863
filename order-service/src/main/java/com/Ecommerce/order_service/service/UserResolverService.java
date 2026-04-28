package com.Ecommerce.order_service.service;

import com.Ecommerce.order_service.dto.response.UserResponse;

public interface UserResolverService {

    UserResponse resolveCurrentUser(String authHeader);

    String getCurrentUserEmail();
}

