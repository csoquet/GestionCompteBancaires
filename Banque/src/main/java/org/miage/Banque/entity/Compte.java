package org.miage.Banque.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Compte implements Serializable {

    @Id
    @Column(name = "iban", nullable = false)
    @JsonIgnore
    private String iban;
    private Double solde;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idclient")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Client client;


}