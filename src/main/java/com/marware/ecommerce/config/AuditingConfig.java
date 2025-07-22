package com.marware.ecommerce.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAware<>() {
            @Override
            public Optional<String> getCurrentAuditor() {
                Authentication auth = SecurityContextHolder
                        .getContext()
                        .getAuthentication();
                if (auth == null || !auth.isAuthenticated()) {
                    return Optional.empty();
                }
                return Optional.of(auth.getName());
            }
        };
    }
}
