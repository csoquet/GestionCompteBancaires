package org.miage.Banque.entity;

import java.io.Serializable;
import java.util.Date;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idoperation", nullable = false)
    private String idoperation;
    private Date dateheure;
    private String libelle;
    private Double montant;
    private Double tauxapplique;
    private String categorie;
    private String pays;

    @JoinColumn(name = "idcompte", insertable = false, updatable = false)
    @ManyToOne(targetEntity = Compte.class, fetch = FetchType.EAGER)
    private Compte compte;


}
