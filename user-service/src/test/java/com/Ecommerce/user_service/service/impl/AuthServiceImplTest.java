package com.Ecommerce.user_service.service.impl;


import com.Ecommerce.user_service.dto.request.LoginRequest;
import com.Ecommerce.user_service.dto.response.LoginResponse;
import com.Ecommerce.user_service.entity.Permission;
import com.Ecommerce.user_service.entity.Role;
import com.Ecommerce.user_service.entity.User;
import com.Ecommerce.user_service.exception.InvalidCredentialsException;
import com.Ecommerce.user_service.repository.UserRepository;
import com.Ecommerce.user_service.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    @Test
    public void login_throwsException_UserNotFound() {

        LoginRequest request = new LoginRequest(
                "user234@gmail.com",
                "password"
        );
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authServiceImpl.login(request));

        verify(userRepository).findByEmail(request.getEmail());
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtUtil);

    }

    @Test
    public void login_throwsException_PasswordInvalid() {

        LoginRequest request = new LoginRequest(
          "user66773@gamil.com",
          "wrong-password"
        );
        Role role = new Role();
        role.setName("ROLE_USER");
        role.setPermissions(Set.of());
        User user = User.builder()
                .id(1L)
                .email("user66773@gamil.com")
                .password("wrong-password")
                .role(role)
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(request.getPassword(),user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authServiceImpl.login(request));

        verify(passwordEncoder).matches(request.getPassword(),user.getPassword());
        verifyNoInteractions(jwtUtil);
    }

    @Test
    public void login_returnsLoginResponse_forValidCredentials() {

        LoginRequest request = new LoginRequest(
                "user@test.com",
                "password"
        );

        Permission p1 = Permission.builder().name("READ").build();
        Permission p2 = Permission.builder().name("WRITE").build();

        Role role = new Role();
        role.setName("USER");
        role.setPermissions(Set.of(p1, p2));

        User user = User.builder()
                .id(10L)
                .email("user@test.com")
                .password("encoded-password")
                .role(role)
                .build();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )).thenReturn(true);

        when(jwtUtil.generateToken(
                eq("user@test.com"),
                eq("USER"),
                anyList()
        )).thenReturn("jwt-token");

        LoginResponse response = authServiceImpl.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("user@test.com", response.getUser().getEmail());
        assertEquals("USER", response.getUser().getRole());

        verify(jwtUtil).generateToken(
                eq("user@test.com"),
                eq("USER"),
                anyList()
        );
    }




}
