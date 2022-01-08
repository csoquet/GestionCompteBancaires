package org.miage.Banque.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idclient", nullable = false)
    @JsonIgnore
    private String idclient;
    private String nom;
    private String prenom;
    private String secret;
    private String datenaiss;
    private String pays;
    @Column(unique = true)
    private String nopasseport;
    private String numtel;





}
