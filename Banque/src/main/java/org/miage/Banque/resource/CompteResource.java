package org.miage.Banque.resource;

import org.miage.Banque.entity.Client;
import org.miage.Banque.entity.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "comptes")
public interface CompteResource extends JpaRepository<Compte, String> {

    Iterable<Compte> findAllByClient(Optional<Client> client);
    Compte findByIban(String iban);
    Optional<Compte> findByClientAndIban(Client client, String iban);
    Boolean existsByClientAndIban(Client client, String iban);
}