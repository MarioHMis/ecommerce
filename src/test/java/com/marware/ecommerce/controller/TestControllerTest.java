package com.marware.ecommerce.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TestController.class)
class TestControllerTest {

    @Autowired MockMvc mvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminEndpoint_shouldAllowAdmin() throws Exception {
        mvc.perform(get("/api/test/admin"))
                .andExpect(status().isOk())
                .andExpect(content().string("Solo los ADMIN pueden ver esto"));
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void sellerEndpoint_shouldAllowSeller() throws Exception {
        mvc.perform(get("/api/test/seller"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void buyerEndpoint_shouldAllowBuyer() throws Exception {
        mvc.perform(get("/api/test/buyer"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void anyEndpoint_shouldAllowAdminOrSeller() throws Exception {
        mvc.perform(get("/api/test/any"))
                .andExpect(status().isOk());
    }
}
