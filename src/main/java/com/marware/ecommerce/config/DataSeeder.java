package com.marware.ecommerce.config;

import com.marware.ecommerce.model.Role;
import com.marware.ecommerce.model.Tenant;
import com.marware.ecommerce.model.User;
import com.marware.ecommerce.repository.ProductRepository;
import com.marware.ecommerce.repository.RoleRepository;
import com.marware.ecommerce.repository.TenantRepository;
import com.marware.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 0. Limpieza
        productRepository.deleteAll();
        userRepository.deleteAll();
        tenantRepository.deleteAll();
        roleRepository.deleteAll();

        // 1. Crear roles
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN");
        Role sellerRole = createRoleIfNotFound("ROLE_SELLER");
        Role customerRole = createRoleIfNotFound("ROLE_CUSTOMER");

        // 2. Crear tenant
        Tenant tenant = new Tenant();
        tenant.setName("Demo Store");
        tenant.setDescription("Demo tenant for testing");
        tenant = tenantRepository.save(tenant);

        // 3. Crear admin
        createUserIfNotExists("admin@example.com", "Administrator", "admin123", tenant, adminRole);

        // 4. Crear seller
        createUserIfNotExists("seller@example.com", "Seller User", "seller123", tenant, sellerRole);

        // 5. Crear customer
        createUserIfNotExists("customer@example.com", "Customer User", "customer123", tenant, customerRole);
    }

    private Role createRoleIfNotFound(String roleName) {
        return roleRepository.findByName(roleName).orElseGet(() -> {
            Role role = new Role();
            role.setName(roleName);
            return roleRepository.save(role);
        });
    }

    private void createUserIfNotExists(String email, String fullName, String rawPassword, Tenant tenant, Role role) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setFullName(fullName);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setTenant(tenant);

            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);

            userRepository.save(user);
        }
    }
}
