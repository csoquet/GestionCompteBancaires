package org.miage.Banque.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Operation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idoperation", nullable = false)
    @JsonIgnore
    private String idoperation;
    @JsonIgnore
    private Date dateheure;
    private String libelle;
    private Double montant;
    private Double tauxapplique;
    private String categorie;
    private String pays;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ibandebiteur")
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Compte comptedebiteur;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ibancrediteur")
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Compte comptecrediteur;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "numcarte")
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CarteBancaire carte;


}
