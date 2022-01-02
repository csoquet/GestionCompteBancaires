package org.miage.Banque.resource;

import org.miage.Banque.entity.CarteBancaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "cartebancaires")
public interface CarteBancaireResource extends JpaRepository<CarteBancaire, String> {
}
