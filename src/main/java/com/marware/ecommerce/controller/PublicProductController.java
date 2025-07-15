package com.marware.ecommerce.controller;

import com.marware.ecommerce.dto.ProductResponse;
import com.marware.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class PublicProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getPublicProducts(
            @RequestParam(required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(productService.getPublicProducts(query, pageable));
    }
}
