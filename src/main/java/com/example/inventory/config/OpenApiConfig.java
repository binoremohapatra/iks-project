package com.example.inventory.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/** Adds the "Authorize" button (Bearer JWT) to Swagger UI. */
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Product Inventory API",
        version = "1.0",
        description = "Secure product catalog API with JWT authentication"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
