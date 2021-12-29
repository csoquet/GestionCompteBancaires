package org.miage.Banque.entity;

import java.io.Serializable;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Operation implements Serializable {

    @Id
    private String idoperation;
    private String dateHeure;
    private Double montant;
    private Double tauxapplique;
    private String categorie;
    private String pays;

    @JoinColumn(name = "idcompte", insertable = false, updatable = false)
    @ManyToOne(targetEntity = Compte.class, fetch = FetchType.EAGER)
    private Compte comptecrediteur;

    @Column(name = "idcompte")
    private String idcompte;
}
