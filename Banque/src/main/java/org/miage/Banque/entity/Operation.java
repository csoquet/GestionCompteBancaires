package org.miage.Banque.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @JoinColumn(name = "idcomptecrediteur")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Compte comptedebiteur;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idcomptedebiteur")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Compte comptecrediteur;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idcarte")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private CarteBancaire carte;


}
