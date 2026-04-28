package com.Ecommerce.product_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String secretKey;

    @BeforeEach
    void startingMethod() {
        jwtUtil = new JwtUtil();

        // must be >= 32 bytes for HS256
        secretKey = "product-secret-key-product-secret-key";

        ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", secretKey);
    }

    private String generateTestToken() {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .setSubject("user@test.com")
                .claim("role", "ADMIN")
                .claim("permissions", List.of("CREATE_PRODUCT", "DELETE_PRODUCT"))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void isTokenValid_shouldReturnTrue_forValidToken() {
        String token = generateTestToken();

        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void isTokenValid_shouldReturnFalse_forInvalidToken() {
        String invalidToken = "invalid.jwt.token";

        assertFalse(jwtUtil.isTokenValid(invalidToken));
    }

    @Test
    void extractEmail_shouldReturnCorrectEmail() {
        String token = generateTestToken();

        String email = jwtUtil.extractEmail(token);

        assertEquals("user@test.com", email);
    }

    @Test
    void extractRole_shouldReturnCorrectRole() {
        String token = generateTestToken();

        String role = jwtUtil.extractRole(token);

        assertEquals("ADMIN", role);
    }

    @Test
    void extractPermissions_shouldReturnPermissions() {
        String token = generateTestToken();

        List<String> permissions = jwtUtil.extractPermissions(token);

        assertEquals(2, permissions.size());
        assertTrue(permissions.contains("CREATE_PRODUCT"));
        assertTrue(permissions.contains("DELETE_PRODUCT"));
    }
}
