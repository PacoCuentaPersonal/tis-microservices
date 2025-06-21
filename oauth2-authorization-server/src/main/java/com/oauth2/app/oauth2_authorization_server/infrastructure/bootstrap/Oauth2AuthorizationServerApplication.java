package com.oauth2.app.oauth2_authorization_server.infrastructure.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan(
        basePackages = "com.oauth2.app.oauth2_authorization_server.infrastructure.config"
)
@EnableFeignClients(basePackages = "com.oauth2.app.oauth2_authorization_server")
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = "com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.repository")
@EntityScan(basePackages = "com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity")
@ComponentScan(basePackages = "com.oauth2.app.oauth2_authorization_server") // Esto es CRÍTICO
public class Oauth2AuthorizationServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2AuthorizationServerApplication.class, args);
    }
}