package com.jcs.fileservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("File service API")
                        .version("1.0")
                        .description("API Documentation for File service")
                        .contact(new Contact()
                                .name("Jianfranco Team")
                                .email("cuentapersonak@gmail.com")));
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