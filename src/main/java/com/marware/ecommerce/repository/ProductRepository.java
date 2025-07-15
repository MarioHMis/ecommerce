package com.marware.ecommerce.repository;

import com.marware.ecommerce.model.Product;
import com.marware.ecommerce.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    List<Product> findAllBySeller(User seller);

    @Query("SELECT p FROM Product p WHERE " +
            "(:search IS NULL OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> searchProducts(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.tenant.id = :tenantId")
    Page<Product> findAllByTenant(Long tenantId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.stock > 0 AND (" +
            ":search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> searchPublicProducts(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.stock > 0")
    List<Product> findAllWithStockAvailable();

    @Query("SELECT p FROM Product p WHERE p.stock > 0")
    Page<Product> findAllWithStockAvailable(Pageable pageable);

}
