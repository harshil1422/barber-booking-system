package com.newlook.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userServiceOpenApi() {
        return new OpenAPI().info(
                new Info()
                        .title("NewLook - User Service")
                        .description("Manages user profiles, roles, and preferences")
                        .version("v1.0.0")
        );
    }
}
