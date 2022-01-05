package org.miage.Banque.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.miage.Banque.entity.CarteBancaire;
import org.miage.Banque.entity.Compte;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class OperationInput {

    @JsonIgnore
    private Date dateheure;
    @Size(min = 5, max = 60)
    private String libelle;
    @NotNull
    @Positive
    private Double montant;
    @NotNull
    @Positive
    private Double tauxapplique;
    private String categorie;
    @Size(min = 2)
    private String pays;

    private String comptecrediteurIban;

    private String carteNumero;
}
