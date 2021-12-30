package org.miage.Banque.input;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    private Date datenaiss;
    @Size(min = 3)
    private String pays;
    private String nopasseport;
    @Size(min = 10, max = 10)
    @Pattern(regexp = "[0-9]+")
    private String numtel;
}
