package org.miage.Banque.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.miage.Banque.entity.CarteBancaire;
import org.miage.Banque.entity.Client;
import org.miage.Banque.entity.Operation;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompteInput {

    @NotNull
    private String iban;
    private Double solde;
    @NotNull
    private Client client;
    private Set<Operation> operation;
    private Set<CarteBancaire> cartes;
}
