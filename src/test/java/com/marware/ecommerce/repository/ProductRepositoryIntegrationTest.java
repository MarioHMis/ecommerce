package com.marware.ecommerce.repository;

import com.marware.ecommerce.model.Product;
import com.marware.ecommerce.model.Tenant;
import com.marware.ecommerce.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ProductRepositoryIntegrationTest {

    @Autowired ProductRepository productRepo;
    @Autowired UserRepository    userRepo;
    @Autowired TenantRepository  tenantRepo;

    @Test
    void saveAndFindById() {
        Tenant t = tenantRepo.save(new Tenant(null, "Store", "Desc"));
        User   u = userRepo.save(User.builder()
                .email("x@y")
                .password("p")
                .fullName("U")
                .tenant(t)
                .build());

        Product p = productRepo.save(Product.builder()
                .name("P")
                .description("D")
                .price(BigDecimal.ONE)
                .stock(1)
                .seller(u)
                .tenant(t)
                .build());
        assertThat(productRepo.findById(p.getId())).isPresent();
    }
}
