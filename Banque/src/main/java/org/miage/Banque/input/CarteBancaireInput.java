package org.miage.Banque.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarteBancaireInput {

    private Boolean localisation;
    @NotNull
    @Positive
    private Double plafond;
    private Boolean sanscontact;
    private Boolean virtuelle;
}
