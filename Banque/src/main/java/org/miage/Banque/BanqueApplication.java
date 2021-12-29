package org.miage.Banque;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BanqueApplication {

	public static void main(String[] args) {
		SpringApplication.run(BanqueApplication.class, args);
	}

	//Génération d'une documentation pour l'API du projet.
	//Disponible à l'URL suivante : http://127.0.0.1:8082/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
	@Bean
	public OpenAPI BanqueAPI(){
		return new OpenAPI().info(new Info()
				.title("Banque API")
				.version("1.0")
				.description("Documentation du projet Gestion compte bancaires."));
	}
}
