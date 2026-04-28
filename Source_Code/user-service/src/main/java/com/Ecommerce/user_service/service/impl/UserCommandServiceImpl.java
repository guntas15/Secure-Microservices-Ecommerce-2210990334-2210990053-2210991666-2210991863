package com.Ecommerce.user_service.service.impl;

import com.Ecommerce.user_service.dto.request.RegisterRequest;
import com.Ecommerce.user_service.dto.response.UserResponse;
import com.Ecommerce.user_service.entity.Role;
import com.Ecommerce.user_service.entity.User;
import com.Ecommerce.user_service.exception.UserAlreadyExistsException;
import com.Ecommerce.user_service.repository.RoleRepository;
import com.Ecommerce.user_service.repository.UserRepository;
import com.Ecommerce.user_service.service.UserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered");
        }
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().getName())
                .build();
    }
}
