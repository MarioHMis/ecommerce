package com.marware.ecommerce.dto;

import com.marware.ecommerce.model.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private String sellerName;
    private String tenantName;

    // Static factory method to convert Product entity to ProductResponse DTO
    public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                product.getSeller().getFullName(),
                product.getTenant().getName()
        );
    }
}
