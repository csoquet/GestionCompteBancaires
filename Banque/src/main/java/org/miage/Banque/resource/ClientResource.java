package org.miage.Banque.resource;

import org.miage.Banque.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "clients")
public interface ClientResource extends JpaRepository<Client, String> {

    Client findByEmail(String email);
}
