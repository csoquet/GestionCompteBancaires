package org.miage.Banque.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.miage.Banque.entity.Client;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompteInput {

    @NotNull
    @Size(min = 14, max = 34)
    @Pattern(regexp = "[A-Z]{2}[0-9]+")
    private String iban;
    @NotNull
    private Double solde;
    @JsonIgnore
    private Client client;
}
