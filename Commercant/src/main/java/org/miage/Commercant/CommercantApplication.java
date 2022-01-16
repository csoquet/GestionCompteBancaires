package org.miage.Commercant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CommercantApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommercantApplication.class, args);
	}


}
