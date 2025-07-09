package com.marware.ecommerce.repository;

import com.marware.ecommerce.model.Product;
import com.marware.ecommerce.model.Tenant;
import com.marware.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByTenant(Tenant tenant);

    List<Product> findAllBySeller(User seller);
}
