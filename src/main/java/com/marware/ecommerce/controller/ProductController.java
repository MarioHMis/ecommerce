package com.marware.ecommerce.controller;

import com.marware.ecommerce.dto.ProductRequest;
import com.marware.ecommerce.dto.ProductResponse;
import com.marware.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.marware.ecommerce.security.RoleConstants.*;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize(HAS_ADMIN_OR_SELLER)
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/mine")
    @PreAuthorize(HAS_ADMIN_OR_SELLER)
    public ResponseEntity<List<ProductResponse>> getMyProducts() {
        List<ProductResponse> products = productService.getProductsBySeller();
        return ResponseEntity.ok(products);
    }
}
