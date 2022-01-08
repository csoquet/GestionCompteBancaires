package org.miage.Banque.resource;

import org.miage.Banque.entity.CarteBancaire;
import org.miage.Banque.entity.Client;
import org.miage.Banque.entity.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "cartebancaires")
public interface CarteBancaireResource extends JpaRepository<CarteBancaire, String> {
    Iterable<CarteBancaire> findAllByCompteAndSupprimerFalse(Optional<Compte> compte);
    Optional<CarteBancaire> findByNumcarteAndCompte(String numcarte, Compte compte);
    Boolean existsByNumcarteAndCompte(String numcarte, Compte compte);
}
