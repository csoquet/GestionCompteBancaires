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
public class CarteBancaire implements Serializable{


    @Id
    private String idcarte;
    private String numcarte;
    private String code;
    private String crypto;
    private Boolean bloque;
    private Boolean localisation;
    private Double plafond;
    private Boolean sanscontact;
    private Boolean virtuelle;

    @JoinColumn(name = "idoperation", insertable = false, updatable = false)
    @ManyToOne(targetEntity = Operation.class, fetch = FetchType.EAGER)
    private Operation operation;

    @Column(name = "idoperation")
    private String idoperation;


}
