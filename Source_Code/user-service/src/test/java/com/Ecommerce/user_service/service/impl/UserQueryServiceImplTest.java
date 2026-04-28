package com.Ecommerce.user_service.service.impl;

import com.Ecommerce.user_service.dto.response.UserResponse;
import com.Ecommerce.user_service.entity.Role;
import com.Ecommerce.user_service.entity.User;
import com.Ecommerce.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserQueryServiceImpl userQueryService;

    @Test
     public void getUserByEmail_returnsUserResponse_UserExists() {

        String email = "user@test.com";

        Role role = new Role();
        role.setName("USER");

        User user = User.builder()
                .id(1L)
                .email(email)
                .role(role)
                .build();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        UserResponse response = userQueryService.getUserByEmail(email);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(email, response.getEmail());
        assertEquals("USER", response.getRole());

        verify(userRepository).findByEmail(email);
    }

    @Test
    void getUserByEmail_throwsException_forUserNotFound() {

        String email = "missing@test.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userQueryService.getUserByEmail(email));

        assertEquals("User not found with email: " + email, ex.getMessage());

        verify(userRepository).findByEmail(email);
    }
    @Test
    void getAllUsers_returnsEmptyList_forNoUsersExist() {

        when(userRepository.findAll())
                .thenReturn(List.of());

        List<UserResponse> responses = userQueryService.getAllUsers();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_returnUserResponses_forUsersExist() {

        Role role = new Role();
        role.setName("ADMIN");

        User user1 = User.builder()
                .id(1L)
                .email("a@test.com")
                .role(role)
                .build();

        User user2 = User.builder()
                .id(2L)
                .email("b@test.com")
                .role(role)
                .build();

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        List<UserResponse> responses = userQueryService.getAllUsers();

        assertEquals(2, responses.size());
        assertEquals("a@test.com", responses.get(0).getEmail());
        assertEquals("b@test.com", responses.get(1).getEmail());
        assertEquals("ADMIN", responses.get(0).getRole());

        verify(userRepository).findAll();
    }




}

