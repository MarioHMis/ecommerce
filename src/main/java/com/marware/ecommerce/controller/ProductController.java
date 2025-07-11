package com.marware.ecommerce.controller;

import com.marware.ecommerce.dto.ProductRequest;
import com.marware.ecommerce.dto.ProductResponse;
import com.marware.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.marware.ecommerce.security.RoleConstants.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize(HAS_ADMIN_OR_SELLER)
    public ResponseEntity<ProductResponse> createProduct(
            @RequestPart("product") @Valid ProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        ProductResponse response = productService.createProduct(request, image);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mine")
    @PreAuthorize(HAS_ADMIN_OR_SELLER)
    public ResponseEntity<List<ProductResponse>> getMyProducts() {
        List<ProductResponse> products = productService.getProductsBySeller();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    @PreAuthorize(HAS_ADMIN_OR_SELLER)
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        ProductResponse updated = productService.updateProduct(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getPublicProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(HAS_ADMIN_OR_SELLER)
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
