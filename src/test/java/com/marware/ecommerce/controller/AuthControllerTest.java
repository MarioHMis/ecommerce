package com.marware.ecommerce.controller;

import com.marware.ecommerce.dto.AuthResponse;
import com.marware.ecommerce.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired MockMvc mvc;
    @MockBean AuthService authService;

    @Test
    void login_returnsToken() throws Exception {
        when(authService.login(any())).thenReturn(new AuthResponse("TOK"));
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"u\",\"password\":\"p\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("TOK"));
    }

    @Test
    void register_returnsToken() throws Exception {
        when(authService.register(any())).thenReturn(new AuthResponse("REGTOK"));
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"F\",\"email\":\"e\",\"password\":\"p\",\"tenantId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("REGTOK"));
    }
}
