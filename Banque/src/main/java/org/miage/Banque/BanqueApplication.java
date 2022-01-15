package org.miage.Banque;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.miage.Banque.entity.*;
import org.miage.Banque.resource.CarteBancaireResource;
import org.miage.Banque.resource.CompteResource;
import org.miage.Banque.resource.OperationResource;
import org.miage.Banque.service.ClientService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Date;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

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

	@Bean
	PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(ClientService cs, CompteResource compteResource, CarteBancaireResource carteBancaireResource, OperationResource operationResource) {
		return args -> {

			Role role = new Role("1", "ROLE_USER");
			cs.saveRole(role);
			Role role2 = new Role("2", "ROLE_ADMIN");
			cs.saveRole(role2);


			Client client1 = new Client("1","Dupont","Bruno", "dupont@test.fr","0000", "15-02-1992", "France", "59RF05400", "0625123621", new ArrayList<>());
			client1 = cs.saveClient(client1);
			Client client2 = new Client("2", "test", "test", "papa@test.fr", "1234", "28-09-1997", "France", "63AL05460","0621513421", new ArrayList<>());
			client2 = cs.saveClient(client2);




			Compte compte1 = new Compte("FR7612548029989876543210917", 500.0, client2);
			Compte compte2 = new Compte("FR7630003035409876543210925", 200.0, client2);
			Compte compte3 = new Compte("FR7630004028379876543210943", 1000.0, client1);
			compteResource.save(compte1);
			compteResource.save(compte2);
			compteResource.save(compte3);

			CarteBancaire carte1 = new CarteBancaire("1234567891234567", "1234", "123", FALSE, FALSE, 1000.0, TRUE, FALSE,"15-03-2022", FALSE, compte1);
			CarteBancaire carte2 = new CarteBancaire("1234567891235457", "4321", "321", TRUE, FALSE, 2000.0, FALSE, FALSE,"10-05-2023",FALSE, compte2);
			carteBancaireResource.save(carte1);
			carteBancaireResource.save(carte2);

			Operation operation1 = new Operation("1",new Date(2022-01-01),"Courses", 20.0, 1.0, "Magasin", "France", compte1,compte2, carte1);
			Operation operation2 = new Operation ("2",new Date(2020-02-10),"Achat medicaments", 10.0, 0.90, "Pharmacie", "Allemagne",compte2, compte1, carte2);
			operationResource.save(operation1);
			operationResource.save(operation2);

			client2 = cs.addRoleToClient("papa@test.fr", "ROLE_ADMIN");

		};
	}
}
