package com.marware.ecommerce.controller;

import com.marware.ecommerce.dto.ProductResponse;
import com.marware.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/products")
@RequiredArgsConstructor
public class PublicProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> listPublicProducts(
            @RequestParam(required = false) String query,
            Pageable pageable) {

        Page<ProductResponse> page = productService.getPublicProducts(query, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getPublicProduct(@PathVariable Long id) {
        ProductResponse response = productService.getPublicProductById(id);
        return ResponseEntity.ok(response);
    }
}
