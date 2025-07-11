package com.marware.ecommerce.service;

import com.marware.ecommerce.dto.ProductRequest;
import com.marware.ecommerce.dto.ProductResponse;
import com.marware.ecommerce.exception.ProductNotFoundException;
import com.marware.ecommerce.exception.UnauthorizedOperationException;
import com.marware.ecommerce.model.Product;
import com.marware.ecommerce.model.User;
import com.marware.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
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
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setSeller(seller);
        product.setTenant(seller.getTenant());
        if (image != null && !image.isEmpty()) {
            product.setImageUrl(fileService.uploadFile(image));
        }
        return mapToProductResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
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
        Product product = getProductIfOwnerOrAdmin(productId);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        return mapToProductResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long productId) {
        productRepository.delete(getProductIfOwnerOrAdmin(productId));
    }

    private Product getProductIfOwnerOrAdmin(Long productId) {
        User currentUser = authService.getAuthenticatedUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        if (!product.getSeller().equals(currentUser) && !currentUser.isAdmin()) {
            throw new UnauthorizedOperationException("No tienes permisos para modificar este producto");
        }
        return product;
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
