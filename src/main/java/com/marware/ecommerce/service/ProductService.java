package com.marware.ecommerce.service;

import com.marware.ecommerce.dto.ProductRequest;
import com.marware.ecommerce.dto.ProductResponse;
import com.marware.ecommerce.exception.*;
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

    // Configuraciones de archivos
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
                .orElseThrow(() -> new EntityNotFoundException("Producto", id));
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
                .orElseThrow(() -> new EntityNotFoundException("Producto", productId));

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
                .orElseThrow(() -> new EntityNotFoundException("Producto", productId));

        validateProductOwnership(product);
        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String query, Pageable pageable) {
        return productRepository.searchProducts(query, pageable)
                .map(this::mapToProductResponse);
    }

    /* === Métodos auxiliares === */

    private void validateProductName(String name, Long excludeId) {
        if (excludeId == null) {
            if (productRepository.existsByName(name)) {
                throw new ValidationException(
                        "DUPLICATE_PRODUCT",
                        "Ya existe un producto con este nombre",
                        List.of("name: El nombre debe ser único")
                );
            }
        } else {
            if (productRepository.existsByNameAndIdNot(name, excludeId)) {
                throw new ValidationException(
                        "DUPLICATE_PRODUCT",
                        "El nuevo nombre ya está en uso por otro producto",
                        List.of("name: El nombre debe ser único")
                );
            }
        }
    }

    private void validateProductOwnership(Product product) {
        User currentUser = authService.getAuthenticatedUser();
        if (!product.getSeller().getId().equals(currentUser.getId()) && !currentUser.isAdmin()) {
            throw new UnauthorizedException(
                    "OPERATION_NOT_ALLOWED",
                    "No tienes permisos para modificar este producto"
            );
        }
    }

    private String uploadImage(MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            try {
                // Validación de tamaño
                if (image.getSize() > MAX_FILE_SIZE) {
                    throw new FileProcessingException(
                            "FILE_TOO_LARGE",
                            "El tamaño del archivo excede el límite de 5MB",
                            null
                    );
                }

                // Validación de tipo de archivo
                if (!ALLOWED_CONTENT_TYPES.contains(image.getContentType())) {
                    throw new FileProcessingException(
                            "INVALID_FILE_TYPE",
                            "Solo se permiten imágenes JPEG, PNG o WEBP",
                            null
                    );
                }

                return fileService.uploadFile(image);
            } catch (FileProcessingException ex) {
                throw ex; // Re-lanzar excepciones específicas
            } catch (Exception e) {
                throw new FileProcessingException(
                        "FILE_UPLOAD_ERROR",
                        "Error al subir la imagen: " + e.getMessage(),
                        e
                );
            }
        }
        return null;
    }

    @Transactional
    public ProductResponse updateProductImage(Long productId, MultipartFile image) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Producto", productId));

        validateProductOwnership(product);

        String newImageUrl = uploadImage(image);
        product.setImageUrl(newImageUrl);

        return mapToProductResponse(productRepository.save(product));
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
