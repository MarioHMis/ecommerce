package com.marware.ecommerce.controller;

import com.marware.ecommerce.dto.ProductResponse;
import com.marware.ecommerce.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired MockMvc mvc;
    @MockBean ProductService productService;

    @Test
    void createProduct_returnsCreatedDto() throws Exception {
        ProductResponse resp = ProductResponse.builder()
                .id(1L)
                .name("Prod")
                .description("Desc")
                .price(BigDecimal.TEN)
                .stock(5)
                .build();

        when(productService.createProduct(any(), any())).thenReturn(resp);

        MockMultipartFile image = new MockMultipartFile(
                "image", "f.png", "image/png", new byte[]{1,2,3});
        MockMultipartFile product = new MockMultipartFile(
                "product", "", "application/json",
                "{\"name\":\"Prod\",\"description\":\"Desc\",\"price\":10,\"stock\":5}"
                        .getBytes());

        mvc.perform(multipart("/api/products")
                        .file(image)
                        .file(product)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Prod"));
    }
}
