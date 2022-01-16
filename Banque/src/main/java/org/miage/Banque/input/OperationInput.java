package org.miage.Banque.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class OperationInput {
    @Size(min = 5, max = 60)
    private String libelle;
    @NotNull
    @Positive
    private Double montant;
    @Positive
    private Double tauxapplique;

    private String categorie;
    @Size(min = 2)
    private String pays;

    @NotNull
    private String comptecrediteurIban;
    private String carteNumero;
    private String codeCarte;

    public OperationInput(double montant, String iban) {
        this.montant = montant;
        this.comptecrediteurIban = iban;
    }
}
