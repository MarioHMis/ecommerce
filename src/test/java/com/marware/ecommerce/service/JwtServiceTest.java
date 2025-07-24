package com.marware.ecommerce.service;

import com.marware.ecommerce.model.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Clave secreta de prueba (Base64)
        String rawKey = "0123456789ABCDEF0123456789ABCDEF"; // 32 bytes
        String secretKey = Base64.getEncoder().encodeToString(rawKey.getBytes());
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "expirationMs", 500L);
    }

    @Test
    void generateToken_and_extractUsername() {
        User user = new User();
        user.setEmail("u@e.com");
        user.setId(42L);
        user.setRoles(Collections.emptySet());

        String token = jwtService.generateToken(user);
        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        assertEquals("u@e.com", username);
    }

    @Test
    void tokenExpiresCorrectly() throws InterruptedException {
        User user = new User();
        user.setEmail("u@e.com");
        user.setId(42L);
        user.setRoles(Collections.emptySet());

        String token = jwtService.generateToken(user);
        Thread.sleep(600); // esperamos a que expire

        Claims claims = jwtService.extractAllClaims(token);
        assertTrue(claims.getExpiration().before(new Date()));
    }
}
