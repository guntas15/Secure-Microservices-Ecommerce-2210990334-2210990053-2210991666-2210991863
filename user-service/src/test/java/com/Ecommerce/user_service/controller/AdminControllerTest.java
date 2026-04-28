package com.Ecommerce.user_service.controller;

import com.Ecommerce.user_service.dto.response.UserResponse;
import com.Ecommerce.user_service.security.JwtAuthenticationFilter;
import com.Ecommerce.user_service.security.JwtUtil;
import com.Ecommerce.user_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // 🔑 REQUIRED to avoid context failure
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void getAllUsers_returnsListOfUsers() throws Exception {

        List<UserResponse> users = List.of(
                UserResponse.builder()
                        .id(1L)
                        .email("user1@test.com")
                        .role("USER")
                        .build(),
                UserResponse.builder()
                        .id(2L)
                        .email("admin@test.com")
                        .role("ADMIN")
                        .build()
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/admin")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("user1@test.com"))
                .andExpect(jsonPath("$[1].role").value("ADMIN"));
    }
}
