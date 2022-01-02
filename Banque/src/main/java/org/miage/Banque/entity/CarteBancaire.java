package org.miage.Banque.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcarte", nullable = false)
    private String idcarte;
    private String numcarte;
    private String code;
    private String crypto;
    private Boolean bloque;
    private Boolean localisation;
    private Double plafond;
    private Boolean sanscontact;
    private Boolean virtuelle;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idcompte")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Compte compte;



}
