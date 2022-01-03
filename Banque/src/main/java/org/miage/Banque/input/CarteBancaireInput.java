package org.miage.Banque.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.miage.Banque.entity.Compte;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarteBancaireInput {

    @Size(min = 16, max = 16)
    @Pattern(regexp = "[0-9]+")
    private String numcarte;
    @Size(min = 4, max = 4)
    @Pattern(regexp = "[0-9]+")
    private String code;
    @Size(min = 3, max = 3)
    @Pattern(regexp = "[0-9]+")
    private String crypto;
    private Boolean bloque;
    private Boolean localisation;
    @NotNull
    private Double plafond;
    private Boolean sanscontact;
    private Boolean virtuelle;
    @NotNull
    private Compte compte;
}
