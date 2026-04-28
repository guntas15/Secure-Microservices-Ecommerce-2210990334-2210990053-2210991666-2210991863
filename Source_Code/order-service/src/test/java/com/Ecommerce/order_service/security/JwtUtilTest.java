package com.Ecommerce.order_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String SECRET =
            "this_is_a_very_secure_secret_key_for_jwt_testing_123456";

    private String token;

    //3 test cases

    @BeforeEach
    void startingMethod() {
        jwtUtil = new JwtUtil();

        // Inject SECRET_KEY manually
        ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", SECRET);

        Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

        token = Jwts.builder()
                .setSubject("test@test.com")
                .claim("role", "USER")
                .claim("permissions", List.of("CREATE_ORDER", "CANCEL_ORDER"))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void extractEmail_returnsSubject() {

        String email = jwtUtil.extractEmail(token);

        assertEquals("test@test.com", email);
    }

    @Test
    void extractRole_returnsRoleClaim() {

        String role = jwtUtil.extractRole(token);

        assertEquals("USER", role);
    }

    @Test
    void extractPermissions_returnsPermissionsList() {

        List<String> permissions = jwtUtil.extractPermissions(token);

        assertEquals(2, permissions.size());
        assertTrue(permissions.contains("CREATE_ORDER"));
        assertTrue(permissions.contains("CANCEL_ORDER"));
    }
}
