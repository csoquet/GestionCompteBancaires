package org.miage.Banque.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.miage.Banque.entity.Compte;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarteBancaireInput {

    private Boolean bloque;
    private Boolean localisation;
    @NotNull
    @Positive
    private Double plafond;
    private Boolean sanscontact;
    private Boolean virtuelle;
    @JsonIgnore
    private Compte compte;
}
