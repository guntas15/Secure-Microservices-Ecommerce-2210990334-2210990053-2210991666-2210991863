package com.Ecommerce.payment_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String SECRET_KEY =
            "my-test-secret-key-my-test-secret-key";

    @BeforeEach
    void startingMethod() {

        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", SECRET_KEY);
    }


    private String generateToken() {

        return Jwts.builder()
                .setSubject("user@test.com")
                .claim("role", "USER")
                .claim("permissions", List.of("PAYMENT_CREATE", "PAYMENT_VIEW"))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .compact();
    }
    @Test
    void extractsEmailFromValidToken() {
        String token = generateToken();

        String email = jwtUtil.extractEmail(token);

        assertNotNull(email);
        assertEquals("user@test.com", email);
    }


    @Test
    void extractsRoleToken(){
        String token = generateToken();
        String role = jwtUtil.extractRole(token);
        assertEquals("USER",role);

        }

     @Test
    void extractsPermissionFromToken(){
        String token = generateToken();

        List<String> permissions = jwtUtil.extractPermissions(token);

        assertNotNull(permissions);
        assertEquals(2,permissions.size());
        assertTrue(permissions.contains("PAYMENT_CREATE"));
    }

    @Test
    void throwsExceptionForInvalidToken(){
        String invalidToken = "invaild.jwt.token";
        assertThrows(Exception.class, ()->
                jwtUtil.extractEmail(invalidToken));

    }
    @Test
    void failsWhenSecretKeyIsWrong(){
        String token = generateToken();

        ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", "wrong-secret-key-wrong-secret-key");
        assertThrows(Exception.class, ()->
                jwtUtil.extractEmail(token));
    }


}