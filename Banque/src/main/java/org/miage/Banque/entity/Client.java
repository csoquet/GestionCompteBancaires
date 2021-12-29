package org.miage.Banque.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client implements Serializable {

    @Id
    private String idclient;
    private String nom;
    private String prenom;
    private String mdp;
    private String datenaiss;
    private String pays;
    private String nopasseport;
    private String numtel;
}
