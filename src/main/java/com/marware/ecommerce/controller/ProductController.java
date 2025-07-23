package com.marware.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marware.ecommerce.dto.ProductRequest;
import com.marware.ecommerce.dto.ProductResponse;
import com.marware.ecommerce.service.ProductService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(
            @RequestPart("product") String productJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        try {
            ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);
            Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }

            ProductResponse created = productService.createProduct(request, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (ConstraintViolationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Invalid product JSON", e);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam(required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(query, pageable));
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ProductResponse>> getProductsBySeller() {
        return ResponseEntity.ok(productService.getProductsBySeller());
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateProductImage(
            @PathVariable Long productId,
            @RequestPart("image") MultipartFile image) {

        return ResponseEntity.ok(productService.updateProductImage(productId, image));
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateProductWithImage(
            @PathVariable Long productId,
            @RequestPart("product") String productJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        try {
            ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);
            Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
            return ResponseEntity.ok(
                    productService.updateProductWithImage(productId, request, image)
            );
        } catch (ConstraintViolationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Invalid product JSON", e);
        }
    }
}
