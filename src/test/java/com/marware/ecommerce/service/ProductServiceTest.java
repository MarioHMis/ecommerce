package com.marware.ecommerce.service;

import com.marware.ecommerce.dto.ProductRequest;
import com.marware.ecommerce.dto.ProductResponse;
import com.marware.ecommerce.exception.FileProcessingException;
import com.marware.ecommerce.exception.ValidationException;
import com.marware.ecommerce.model.Product;
import com.marware.ecommerce.model.Tenant;
import com.marware.ecommerce.model.User;
import com.marware.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuthService authService;

    @Mock
    private FileService fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProduct_whenNameAlreadyExists_thenThrowValidationException() {
        // Given
        ProductRequest request = new ProductRequest();
        request.setName("ExistingProduct");
        request.setDescription("Descripción");
        request.setPrice(BigDecimal.valueOf(10));
        request.setStock(5);

        when(productRepository.existsByName("ExistingProduct")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> productService.createProduct(request, null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Ya existe un producto con este nombre");
    }

    @Test
    void createProduct_whenImageTooLarge_thenThrowFileProcessingException() {
        // Given
        ProductRequest request = new ProductRequest();
        request.setName("NewProduct");
        request.setDescription("Descripción");
        request.setPrice(BigDecimal.valueOf(20));
        request.setStock(3);

        when(productRepository.existsByName("NewProduct")).thenReturn(false);

        User seller = new User();
        seller.setId(1L);
        seller.setTenant(new Tenant(1L, "Demo Store", "Desc"));
        when(authService.getAuthenticatedUser()).thenReturn(seller);

        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
        // 6 MB > límite de 5 MB
        when(image.getSize()).thenReturn(6L * 1024 * 1024);
        when(image.getContentType()).thenReturn("image/jpeg");

        // When & Then
        assertThatThrownBy(() -> productService.createProduct(request, image))
                .isInstanceOf(FileProcessingException.class)
                .hasMessageContaining("El tamaño del archivo excede el límite de 5MB");
    }

    @Test
    void createProduct_whenImageInvalidType_thenThrowFileProcessingException() {
        // Given
        ProductRequest request = new ProductRequest();
        request.setName("AnotherProduct");
        request.setDescription("Otra descripción");
        request.setPrice(BigDecimal.valueOf(15));
        request.setStock(10);

        when(productRepository.existsByName("AnotherProduct")).thenReturn(false);

        User seller = new User();
        seller.setId(2L);
        seller.setTenant(new Tenant(2L, "Other Store", "Otra Desc"));
        when(authService.getAuthenticatedUser()).thenReturn(seller);

        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
        when(image.getSize()).thenReturn(1024L);
        // Tipo no permitido
        when(image.getContentType()).thenReturn("application/pdf");

        // When & Then
        assertThatThrownBy(() -> productService.createProduct(request, image))
                .isInstanceOf(FileProcessingException.class)
                .hasMessageContaining("Solo se permiten imágenes JPEG, PNG o WEBP");
    }

    @Test
    void createProduct_whenValidRequestWithoutImage_thenReturnProductResponse() {
        // Given
        ProductRequest request = new ProductRequest();
        request.setName("ValidProduct");
        request.setDescription("Desc válida");
        request.setPrice(BigDecimal.valueOf(50));
        request.setStock(7);

        when(productRepository.existsByName("ValidProduct")).thenReturn(false);

        User seller = new User();
        seller.setId(3L);
        seller.setFullName("Seller Name");
        seller.setTenant(new Tenant(3L, "Store 3", "Desc 3"));
        when(authService.getAuthenticatedUser()).thenReturn(seller);

        Product saved = Product.builder()
                .id(42L)
                .name("ValidProduct")
                .description("Desc válida")
                .price(BigDecimal.valueOf(50))
                .stock(7)
                .seller(seller)
                .tenant(seller.getTenant())
                .imageUrl(null)
                .build();
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        // When
        ProductResponse response = productService.createProduct(request, null);

        // Then
        assertThat(response.getId()).isEqualTo(42);
        assertThat(response.getName()).isEqualTo("ValidProduct");
        assertThat(response.getSellerName()).isEqualTo("Seller Name");
        assertThat(response.getTenantName()).isEqualTo("Store 3");
        assertThat(response.getImageUrl()).isNull();
    }
}
