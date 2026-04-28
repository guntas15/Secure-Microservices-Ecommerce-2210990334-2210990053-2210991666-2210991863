package com.Ecommerce.order_service.client;

import com.Ecommerce.order_service.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface  UserClient {

    @GetMapping("/api/users/by-email")
    UserResponse getUserByEmail(
            @RequestParam String email,
            @RequestHeader("Authorization") String authHeader
    );
}
