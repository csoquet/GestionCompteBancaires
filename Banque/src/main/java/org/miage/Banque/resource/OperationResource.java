package org.miage.Banque.resource;

import org.miage.Banque.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "operations")
public interface OperationResource extends JpaRepository<Operation, String> {
}
