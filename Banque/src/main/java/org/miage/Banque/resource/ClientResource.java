package org.miage.Banque.resource;

import org.miage.Banque.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "clients")
public interface ClientResource extends JpaRepository<Client, String> {

    Client findByEmail(String email);
    Client getByIdclient(String idclient);
    boolean existsByIdclient(String idclient);
}
