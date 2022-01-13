package org.miage.Banque.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import org.miage.Banque.entity.Role;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idclient", nullable = false)
    private String idclient;
    private String nom;
    private String prenom;
    private String email;
    private String secret;
    private String datenaiss;
    private String pays;
    @Column(unique = true)
    private String nopasseport;
    private String numtel;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "client_roles",
            joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "idclient"),
            inverseJoinColumns = @JoinColumn(name = "roles_id",referencedColumnName = "idrole"))
    private Collection<Role> roles = new ArrayList<>();





}
