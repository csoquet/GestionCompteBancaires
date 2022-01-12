package org.miage.Banque.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class OperationInput {
    @NotNull
    @Size(min = 5, max = 60)
    private String libelle;
    @NotNull
    @Positive
    private Double montant;
    @NotNull
    @Positive
    private Double tauxapplique;

    private String categorie;
    @NotNull
    @Size(min = 2)
    private String pays;

    @NotNull
    private String comptecrediteurIban;
    @NotNull
    private String carteNumero;
    private String codeCarte;
}
