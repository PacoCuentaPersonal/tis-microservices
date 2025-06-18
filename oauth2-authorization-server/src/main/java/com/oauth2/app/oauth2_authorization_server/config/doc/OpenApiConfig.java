package com.oauth2.app.oauth2_authorization_server.config.doc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OAuth2 Authorization Server API")
                        .version("1.0")
                        .description("API Documentation for OAuth2 Authorization Server")
                        .contact(new Contact()
                                .name("Your Team")
                                .email("team@example.com")));
//                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
//                .components(new Components()
//                        .addSecuritySchemes("Bearer Authentication",
//                                new SecurityScheme()
//                                        .type(SecurityScheme.Type.HTTP)
//                                        .scheme("bearer")
//                                        .bearerFormat("JWT")
//                                        .description("Provide the JWT token. JWT token can be obtained from the /oauth2/token endpoint.")));
    }
}