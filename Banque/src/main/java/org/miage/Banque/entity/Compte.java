package org.miage.Banque.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Compte implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcompte", nullable = false)
    private String idcompte;
    private String iban;
    private Double solde;

    @OneToOne(targetEntity = Client.class)
    private Client client;

    //@OneToOne(mappedBy = "compte",cascade = CascadeType.ALL)
    //    private Client client;

    @OneToMany(mappedBy = "compte")
    private Set<Operation> operation;

    @OneToMany(mappedBy = "compte")
    private Set<CarteBancaire> cartes;
}