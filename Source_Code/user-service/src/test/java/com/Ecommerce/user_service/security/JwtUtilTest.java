package com.Ecommerce.user_service.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void startingMethod() {
        jwtUtil = new JwtUtil();

        // injection of  @Value fields
        ReflectionTestUtils.setField(
                jwtUtil,
                "secretKey",
                "my-secret-key-my-secret-key-my-secret-key" // >= 32 bytes
        );

        ReflectionTestUtils.setField(
                jwtUtil,
                "expirationTime",
                60_000L // 1 minute only

        );
    }

    @Test
    void generateToken_extractsClaims_workingCheck() {
        String email = "user@test.com";
        String role = "USER";
        List<String> permissions = List.of("READ", "WRITE");

        String token =
                jwtUtil.generateToken(email, role, permissions);

        assertNotNull(token);
        assertEquals(email, jwtUtil.extractEmail(token));
        assertEquals(role, jwtUtil.extractRole(token));
        assertEquals(permissions, jwtUtil.extractPermissions(token));
    }

    @Test
    void isTokenValid_returnsTrue_forValidToken() {
        String token =
                jwtUtil.generateToken(
                        "user@test.com",
                        "USER",
                        List.of("READ")
                );

        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void isTokenValid_returnsFalse_forInvalidToken() {
        String invalidToken = "invalid.jwt.token";

        assertFalse(jwtUtil.isTokenValid(invalidToken));
    }

    @Test
    void isTokenValid_returnsFalse_forExpiredToken() throws InterruptedException {

        ReflectionTestUtils.setField(jwtUtil, "expirationTime", 1L);

        String token =
                jwtUtil.generateToken(
                        "user@test.com",
                        "USER",
                        List.of("READ")
                );

        Thread.sleep(5);

        assertFalse(jwtUtil.isTokenValid(token));
    }
}
