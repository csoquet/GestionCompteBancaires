package org.miage.Banque.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarteBancaire implements Serializable{


    @Id
    @Column(name = "numcarte", nullable = false)
    @JsonIgnore
    private String numcarte;
    private String code;
    private String crypto;
    private Boolean bloque;
    private Boolean localisation;
    private Double plafond;
    private Boolean sanscontact;
    private Boolean virtuelle;
    private String expiration;
    @JsonIgnore
    private Boolean supprimer;



    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "iban")
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Compte compte;



}
