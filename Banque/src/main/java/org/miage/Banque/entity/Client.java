package org.miage.Banque.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String secret;
    private Date datenaiss;
    private String pays;
    private String nopasseport;
    private String numtel;

    @OneToMany
    @JoinColumn(name="idcompte", insertable = false, updatable = false)
    private Set<Compte> comptes;





}
