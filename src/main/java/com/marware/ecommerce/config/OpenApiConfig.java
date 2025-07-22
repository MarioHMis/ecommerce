package com.marware.ecommerce.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title       = "E‑Commerce API",
                version     = "v1",
                description = "REST API for the multi‑tenant E‑Commerce application"
        )
)
public class OpenApiConfig {

}
