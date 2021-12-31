package org.miage.Banque.resource;

import org.miage.Banque.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationResource extends JpaRepository<Operation, String> {
}
