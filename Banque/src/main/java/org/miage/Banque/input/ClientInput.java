package org.miage.Banque.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.miage.Banque.entity.Compte;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientInput {

    @NotNull
    @NotBlank
    private String nom;
    @Size(min=2)
    private String prenom;
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
