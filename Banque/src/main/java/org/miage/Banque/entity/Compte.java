package org.miage.Banque.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Compte implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcompte", nullable = false)
    @JsonIgnore
    private String idcompte;
    @Column(unique = true)
    private String iban;
    private Double solde;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idclient")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Client client;


}