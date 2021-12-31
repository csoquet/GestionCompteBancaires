package org.miage.Banque.resource;

import org.miage.Banque.entity.CarteBancaire;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarteBancaireResource extends JpaRepository<CarteBancaire, String> {
}
