package com.Ecommerce.user_service.controller;

import com.Ecommerce.user_service.dto.request.LoginRequest;
import com.Ecommerce.user_service.dto.request.RegisterRequest;
import com.Ecommerce.user_service.dto.response.ApiResponse;
import com.Ecommerce.user_service.dto.response.LoginResponse;
import com.Ecommerce.user_service.dto.response.UserResponse;
import com.Ecommerce.user_service.service.UserService;
import com.Ecommerce.user_service.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        UserResponse user = userService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        "User registered successfully",
                        user
                ));
    }
    @GetMapping("/by-email")
    public UserResponse getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email);
    }



    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = userService.login(request);

        return ResponseEntity.ok(
                new ApiResponse<>("Login successful", response)
        );
    }


}
