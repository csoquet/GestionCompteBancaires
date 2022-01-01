package org.miage.Banque.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.miage.Banque.entity.Compte;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class OperationInput {

    private Date dateheure;
    @Size(min = 5, max = 60)
    private String libelle;
    private Double montant;
    private Double tauxapplique;
    @Size(min = 5, max = 30)
    private String categorie;
    @Size(min = 2)
    private String pays;
    @NotNull
    private Compte compte;
}
