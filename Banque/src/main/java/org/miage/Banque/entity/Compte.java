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
public class Compte implements Serializable {

    @Id
    private String idcompte;
    private String IBAN;

    @JoinColumn(name = "idclient", insertable = false, updatable = false)
    @ManyToOne(targetEntity = Client.class, fetch = FetchType.EAGER)
    private Client client;

    @Column(name = "idclient")
    private String idclient;
}