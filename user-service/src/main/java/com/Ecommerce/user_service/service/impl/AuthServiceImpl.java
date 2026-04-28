package com.Ecommerce.user_service.service.impl;

import com.Ecommerce.user_service.dto.request.LoginRequest;
import com.Ecommerce.user_service.dto.response.LoginResponse;
import com.Ecommerce.user_service.dto.response.UserResponse;
import com.Ecommerce.user_service.entity.User;
import com.Ecommerce.user_service.exception.InvalidCredentialsException;
import com.Ecommerce.user_service.repository.UserRepository;
import com.Ecommerce.user_service.security.JwtUtil;
import com.Ecommerce.user_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.Ecommerce.user_service.entity.Permission;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        List<String> permissions = user.getRole()
                .getPermissions()
                .stream()
                .map(Permission::getName)
                .toList();


        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().getName(),
                permissions
        );


        return LoginResponse.builder()
                .token(token)
                .user(UserResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole().getName())
                        .build())
                .build();
    }
}

