package org.miage.Banque.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.miage.Banque.entity.Client;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompteInput {

    @NotNull
    private String iban;
    private Double solde;
    @JsonIgnore
    private Client client;
}
