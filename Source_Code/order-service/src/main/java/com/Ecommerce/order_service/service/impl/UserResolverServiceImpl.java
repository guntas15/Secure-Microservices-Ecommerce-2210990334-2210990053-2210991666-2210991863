package com.Ecommerce.order_service.service.impl;

import com.Ecommerce.order_service.client.UserClient;
import com.Ecommerce.order_service.dto.response.UserResponse;
import com.Ecommerce.order_service.service.UserResolverService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserResolverServiceImpl implements UserResolverService {

    private final UserClient userClient;

    @Override
    public UserResponse resolveCurrentUser(String authHeader) {
        String email = getCurrentUserEmail();
        return userClient.getUserByEmail(email, authHeader);
    }

    @Override
    public String getCurrentUserEmail() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}

