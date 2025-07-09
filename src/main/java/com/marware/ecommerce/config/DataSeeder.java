package com.marware.ecommerce.config;

import com.marware.ecommerce.model.Role;
import com.marware.ecommerce.model.Tenant;
import com.marware.ecommerce.model.User;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. Create roles if they do not exist
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_SELLER");
        createRoleIfNotFound("ROLE_CUSTOMER");

        // 2. Create a demo tenant if it does not exist
        Tenant tenant = tenantRepository.findByName("Demo Store")
                .orElseGet(() -> {
                    Tenant newTenant = new Tenant();
                    newTenant.setName("Demo Store");
                    newTenant.setDescription("Demo tenant for testing");
                    return tenantRepository.save(newTenant);
                });

        // 3. Create an admin user if it does not exist
        if (!userRepository.existsByEmail("admin@example.com")) {
            User admin = new User();
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Administrator");
            admin.setTenant(tenant);

            Set<Role> roles = new HashSet<>();
            roleRepository.findByName("ROLE_ADMIN").ifPresent(roles::add);
            admin.setRoles(roles);

            userRepository.save(admin);
        }
    }

    private void createRoleIfNotFound(String roleName) {
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (roleOpt.isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}
