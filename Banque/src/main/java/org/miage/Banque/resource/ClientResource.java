package org.miage.Banque.resource;

import org.miage.Banque.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientResource extends JpaRepository<Client, String> {
}
