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

    @JoinColumn(name = "idclient", insertable = false, updatable = false)
    @ManyToOne(targetEntity = Client.class, fetch = FetchType.EAGER)
    private Client client;



}
