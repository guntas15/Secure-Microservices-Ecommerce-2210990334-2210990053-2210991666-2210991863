package com.Ecommerce.user_service.service.impl;

import com.Ecommerce.user_service.dto.request.RegisterRequest;
import com.Ecommerce.user_service.dto.response.UserResponse;
import com.Ecommerce.user_service.entity.Role;
import com.Ecommerce.user_service.entity.User;
import com.Ecommerce.user_service.exception.UserAlreadyExistsException;
import com.Ecommerce.user_service.repository.RoleRepository;
import com.Ecommerce.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserCommandServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    @Test
    public void register_throwsException_EmailAlreadyExists() {

        RegisterRequest request = new RegisterRequest(
                "existing@test.com",
                "password"
        );

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userCommandService.register(request));

        verify(userRepository).existsByEmail(request.getEmail());
        verifyNoInteractions(roleRepository);
        verifyNoInteractions(passwordEncoder);
    }


    @Test
    void register_throwsException_RoleNotFound() {

        RegisterRequest request = new RegisterRequest(
                "user@test.com",
                "password"
        );

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(roleRepository.findByName("USER"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userCommandService.register(request));

        assertEquals("Role not found", ex.getMessage());

        verify(roleRepository).findByName("USER");
        verifyNoInteractions(passwordEncoder);
    }


    @Test
    void register_returnsUserResponse_forValidRequest() {

        RegisterRequest request = new RegisterRequest(
                "new@test.com",
                "password"
        );

        Role role = new Role();
        role.setId(1);
        role.setName("USER");

        User savedUser = User.builder()
                .id(100L)
                .email("new@test.com")
                .password("encoded-password")
                .role(role)
                .build();

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(roleRepository.findByName("USER"))
                .thenReturn(Optional.of(role));

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        UserResponse response = userCommandService.register(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("new@test.com", response.getEmail());
        assertEquals("USER", response.getRole());

        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(User.class));
    }


}
