package com.Ecommerce.order_service.service.impl;

import com.Ecommerce.order_service.client.UserClient;
import com.Ecommerce.order_service.dto.response.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserResolverServiceImplTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserResolverServiceImpl userResolverService;

    // 2 test cases

    @BeforeEach
    void startingMethod() {
        MockitoAnnotations.openMocks(this);

        // Set Security Context
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "test@test.com",
                        null
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserEmail_returnsEmailFromSecurityContext() {

        String email = userResolverService.getCurrentUserEmail();

        assertEquals("test@test.com", email);
    }

    @Test
    void resolveCurrentUser_callsUserClientWithEmail() {

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .email("test@test.com")
                .role("USER")
                .build();

        when(userClient.getUserByEmail("test@test.com", "token"))
                .thenReturn(userResponse);

        UserResponse response =
                userResolverService.resolveCurrentUser("token");

        assertEquals(1L, response.getId());
        assertEquals("test@test.com", response.getEmail());

        verify(userClient).getUserByEmail("test@test.com", "token");
    }
}
