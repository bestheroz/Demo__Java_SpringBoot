package com.github.bestheroz.standard.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
@io.swagger.v3.oas.annotations.security.SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT")
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .components(
            new Components()
                .addSecuritySchemes(
                    "bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
  }

  @Bean
  public OperationCustomizer customizeOperation() {
    return (Operation operation, HandlerMethod handlerMethod) -> {
      io.swagger.v3.oas.annotations.security.SecurityRequirement securityRequirement =
          handlerMethod.getMethodAnnotation(
              io.swagger.v3.oas.annotations.security.SecurityRequirement.class);

      if (securityRequirement == null) {
        // If @SecurityRequirement is not present, remove any security requirements
        operation.setSecurity(null);
      }

      return operation;
    };
  }
}
