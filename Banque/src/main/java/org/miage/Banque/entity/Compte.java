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
    private String IBAN;
    private Double solde;

    @JoinColumn(name = "idclient", insertable = false, updatable = false)
    @ManyToOne(targetEntity = Client.class, fetch = FetchType.EAGER)
    private Client client;

    @OneToMany
    @JoinColumn(name="idoperation")
    private Set<Operation> operation;

    @OneToMany
    @JoinColumn(name="idcarte")
    private Set<CarteBancaire> cartes;
}