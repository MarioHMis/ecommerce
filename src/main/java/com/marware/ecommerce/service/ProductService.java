package com.marware.ecommerce.service;

import com.marware.ecommerce.dto.ProductRequest;
import com.marware.ecommerce.dto.ProductResponse;
import com.marware.ecommerce.exception.ProductNotFoundException;
import com.marware.ecommerce.exception.UnauthorizedException;
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

    @Transactional
    public ProductResponse createProduct(ProductRequest request, MultipartFile image) {
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

        productRepository.save(product);
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
        return productRepository.findAllBySellerId(seller.getId()).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String query, Pageable pageable) {
        return productRepository.searchProducts(query, pageable)
                .map(this::mapToProductResponse);
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        User currentUser = authService.getAuthenticatedUser();
        Product product = getProductIfOwnerOrAdmin(productId, currentUser);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        return mapToProductResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long productId) {
        User currentUser = authService.getAuthenticatedUser();
        Product product = getProductIfOwnerOrAdmin(productId, currentUser);
        productRepository.delete(product);
    }

    private Product getProductIfOwnerOrAdmin(Long productId, User currentUser) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (!product.getSeller().getId().equals(currentUser.getId()) && !currentUser.isAdmin()) {
            throw new UnauthorizedException("No tienes permisos para modificar este producto");
        }

        return product;
    }

    private String uploadImage(MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            return fileService.uploadFile(image);
        }
        return null;
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
