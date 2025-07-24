package com.marware.ecommerce.security;

import com.marware.ecommerce.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtFilterTest {

    private JwtFilter filter;
    private JwtService jwtService;
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        filter = new JwtFilter(jwtService);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    void whenNoAuthHeader_thenProceedWithoutAuth() throws Exception {
        when(req.getRequestURI()).thenReturn("/api/whatever");
        when(req.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(req, resp, chain);

        verify(chain).doFilter(req, resp);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void whenValidToken_thenPopulateSecurityContext() throws Exception {
        when(req.getRequestURI()).thenReturn("/api/secured");
        when(req.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtService.extractUsername("token123")).thenReturn("user@example.com");
        Claims claims = mock(Claims.class);
        when(jwtService.extractAllClaims("token123")).thenReturn(claims);
        when(claims.get("roles", List.class)).thenReturn(List.of("ROLE_ADMIN"));

        filter.doFilterInternal(req, resp, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("user@example.com",
                SecurityContextHolder.getContext().getAuthentication().getName());
        verify(chain).doFilter(req, resp);
    }
}
