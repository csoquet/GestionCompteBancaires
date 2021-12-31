package org.miage.Banque.resource;

import org.miage.Banque.entity.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompteResource extends JpaRepository<Compte, String> {
}