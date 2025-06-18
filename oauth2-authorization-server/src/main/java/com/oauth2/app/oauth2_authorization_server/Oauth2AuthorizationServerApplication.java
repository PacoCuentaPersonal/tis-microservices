package com.oauth2.app.oauth2_authorization_server;

import com.oauth2.app.oauth2_authorization_server.models.entity.Roles;
import com.oauth2.app.oauth2_authorization_server.interfaces.repository.JpaRolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan(
		basePackages = "com.oauth2.app.oauth2_authorization_server.config"
)
@EnableFeignClients
@EnableDiscoveryClient
public class Oauth2AuthorizationServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(Oauth2AuthorizationServerApplication.class, args);
	}
}

@RequiredArgsConstructor
@Component
class InitRoles implements CommandLineRunner {
	private final JpaRolesRepository rolesRepository;

	@Override
	public void run(String... args) throws Exception {
		ZonedDateTime now = ZonedDateTime.now(); // current time with zone
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

		String formatted = now.format(formatter);
		System.out.println(formatted); // Example: 2025-05-05T16:31:00+0700

		if (rolesRepository.findAll().isEmpty()){
            List<Roles> rolesList = new java.util.ArrayList<>();
            rolesList.add(Roles.builder().name("ADMIN").build());
            rolesList.add(Roles.builder().name("USER").build());
            rolesRepository.saveAll(rolesList);
		}
	}
}
