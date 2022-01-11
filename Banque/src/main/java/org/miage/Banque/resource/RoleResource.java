package org.miage.Banque.resource;

import org.miage.Banque.entity.Client;
import org.miage.Banque.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleResource extends JpaRepository<Role, String> {

    Role findByNom(String nom);

}
