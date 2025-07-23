package com.marware.ecommerce.service;

import com.marware.ecommerce.dto.ProductRequest;
import com.marware.ecommerce.dto.ProductResponse;
import com.marware.ecommerce.exception.EntityNotFoundException;
import com.marware.ecommerce.exception.FileProcessingException;
import com.marware.ecommerce.exception.UnauthorizedException;
import com.marware.ecommerce.exception.ValidationException;
import com.marware.ecommerce.model.Product;
import com.marware.ecommerce.model.User;
import com.marware.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final AuthService authService;
    private final FileService fileService;

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    @Transactional
    public ProductResponse createProduct(ProductRequest request, MultipartFile image) {
        validateProductName(request.getName(), null);

        User seller = authService.getAuthenticatedUser();

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .seller(seller)
                .tenant(seller.getTenant())
                .imageUrl(uploadImage(image))
                .build();

        return mapToProductResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product", id));
        return mapToProductResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapToProductResponse);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsBySeller() {
        User seller = authService.getAuthenticatedUser();
        return productRepository.findAllBySeller(seller).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));

        validateProductOwnership(product);
        validateProductName(request.getName(), productId);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        return mapToProductResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));

        validateProductOwnership(product);

        if (product.getImageUrl() != null) {
            fileService.deleteFile(product.getImageUrl());
        }

        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String query, Pageable pageable) {
        return productRepository.searchProducts(query, pageable)
                .map(this::mapToProductResponse);
    }

    @Transactional
    public ProductResponse updateProductImage(Long productId, MultipartFile image) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));

        validateProductOwnership(product);

        if (product.getImageUrl() != null) {
            fileService.deleteFile(product.getImageUrl());
        }

        String newImageUrl = uploadImage(image);
        product.setImageUrl(newImageUrl);

        return mapToProductResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProductWithImage(Long productId, ProductRequest request, MultipartFile image) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));

        validateProductOwnership(product);
        validateProductName(request.getName(), productId);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        if (image != null && !image.isEmpty()) {
            if (product.getImageUrl() != null) {
                fileService.deleteFile(product.getImageUrl());
            }
            product.setImageUrl(uploadImage(image));
        }

        return mapToProductResponse(productRepository.save(product));
    }

    // --- Public API methods ---

    @Transactional(readOnly = true)
    public Page<ProductResponse> getPublicProducts(String query, Pageable pageable) {
        return productRepository.searchPublicProducts(query, pageable)
                .map(this::mapToProductResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getPublicProductById(Long id) {
        Product product = productRepository.findById(id)
                .filter(p -> p.getStock() != null && p.getStock() > 0)
                .orElseThrow(() -> new EntityNotFoundException("Public Product", id));
        return mapToProductResponse(product);
    }

    // === Helpers ===

    private void validateProductName(String name, Long excludeId) {
        boolean duplicate = (excludeId == null)
                ? productRepository.existsByName(name)
                : productRepository.existsByNameAndIdNot(name, excludeId);

        if (duplicate) {
            throw new ValidationException(
                    "DUPLICATE_PRODUCT",
                    "A product with this name already exists",
                    List.of("name: The name must be unique")
            );
        }
    }

    private void validateProductOwnership(Product product) {
        User currentUser = authService.getAuthenticatedUser();
        if (!product.getSeller().getId().equals(currentUser.getId()) && !currentUser.isAdmin()) {
            throw new UnauthorizedException(
                    "OPERATION_NOT_ALLOWED",
                    "You do not have permission to modify this product"
            );
        }
    }

    private String uploadImage(MultipartFile image) {
        if (image == null || image.isEmpty()) return null;

        if (image.getSize() > MAX_FILE_SIZE) {
            throw new FileProcessingException("FILE_TOO_LARGE", "File exceeds 5MB", null);
        }
        if (!ALLOWED_CONTENT_TYPES.contains(image.getContentType())) {
            throw new FileProcessingException("INVALID_FILE_TYPE", "Only JPEG, PNG or WEBP allowed", null);
        }
        return fileService.uploadFile(image);
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .sellerName(product.getSeller().getFullName())
                .tenantName(product.getTenant().getName())
                .build();
    }
}
