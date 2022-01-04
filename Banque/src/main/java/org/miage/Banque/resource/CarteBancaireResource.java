package org.miage.Banque.resource;

import org.miage.Banque.entity.CarteBancaire;
import org.miage.Banque.entity.Compte;
import org.miage.Banque.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "cartebancaires")
public interface CarteBancaireResource extends JpaRepository<CarteBancaire, String> {
    Iterable<CarteBancaire> findAllByCompte(Optional<Compte> compte);
    CarteBancaire findByNumcarte(String numcarte);
}
