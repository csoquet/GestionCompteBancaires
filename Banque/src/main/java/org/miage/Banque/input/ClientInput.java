package org.miage.Banque.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientInput {

    @NotNull
    @NotBlank
    private String nom;
    @Size(min=2)
    private String prenom;
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", message = "Format de l'email invalide")
    private String email;
    @Pattern(regexp = "[0-9]+")
    private String secret;
    @NotNull
    private String datenaiss;
    @Size(min = 3)
    private String pays;
    @NotNull
    private String nopasseport;
    @Size(min = 10, max = 10)
    @Pattern(regexp = "[0-9]+")
    private String numtel;
}
