package org.miage.Banque.resource;

import org.miage.Banque.entity.CarteBancaire;
import org.miage.Banque.entity.Compte;
import org.miage.Banque.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "operations")
public interface OperationResource extends JpaRepository<Operation, String> {

    Iterable<Operation> findAllByComptedebiteur(Optional<Compte> comptedebiteur);
    List<Operation> findAllByCarte(CarteBancaire carte);
}
