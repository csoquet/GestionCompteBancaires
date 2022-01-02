package org.miage.Banque.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idclient", nullable = false)
    private String idclient;
    private String nom;
    private String prenom;
    private String secret;
    private String datenaiss;
    private String pays;
    private String nopasseport;
    private String numtel;





}
