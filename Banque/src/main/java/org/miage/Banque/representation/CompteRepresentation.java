package org.miage.Banque.representation;

import org.miage.Banque.assembler.CompteAssembler;
import org.miage.Banque.entity.Client;
import org.miage.Banque.entity.Compte;
import org.miage.Banque.input.CompteInput;
import org.miage.Banque.resource.ClientResource;
import org.miage.Banque.resource.CompteResource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value="/clients/{clientId}/comptes", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Compte.class)
public class CompteRepresentation {

    private final ClientResource clientResource;
    private final CompteResource cr;
    private final CompteAssembler ca;

    public CompteRepresentation(ClientResource clientResource, CompteResource cr, CompteAssembler ca) {
        this.clientResource = clientResource;
        this.cr = cr;
        this.ca = ca;
    }


    @GetMapping
    //@PostAuthorize("returnObject.content.clientId == authentication.name or hasRole('ROLE_ADMIN')")
    public CollectionModel<EntityModel<Compte>> getAllComptesByIdClient(@PathVariable("clientId") String clientId) {
        Optional<Client> client = clientResource.findById(clientId);
        Iterable<Compte> comptes =  cr.findAllByClient(client);
        return ca.toCollectionModel(comptes);
    }

    @GetMapping(value = "/{compteIban}")
    @PostAuthorize("returnObject.content.client.email == authentication.name or hasRole('ROLE_ADMIN')")
    public EntityModel getOneCompte(@PathVariable("clientId") String clientId, @PathVariable("compteIban") String id) {
        Client client = clientResource.getByIdclient(clientId);
        if(cr.existsByClientAndIban(client, id)){
            return ca.toModel(cr.findById(id).get());
        }
        throw new RuntimeException("Ce compte n'existe pas pour ce client");

    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveCompte(@PathVariable("clientId") String clientId, @RequestBody @Valid CompteInput compte, @AuthenticationPrincipal String clientEmail) {

        Optional<Client> client = clientResource.findById(clientId);
        if(client.get().getEmail().equals(clientEmail)){
            String lettre = client.get().getPays().substring(0,2).toUpperCase(); //2 premiere lettre du pays
            String iban = lettre;
            int Min = 1;
            int Max = 9;
            for(int i = 1; i <= 25; i++){//Générer des chiffres de l'iban
                int nombreAleatoire = Min + (int)(Math.random() * (Max - Min) + 1);
                iban += nombreAleatoire;
            }
            Compte compteSave = new Compte(
                    iban,
                    compte.getSolde(),
                    client.get()
            );
            Compte saved = cr.save(compteSave);
            URI location = linkTo(methodOn(CompteRepresentation.class).getOneCompte(clientId, saved.getIban())).toUri();
            return ResponseEntity.created(location).build();
        }
        throw new RuntimeException("Impossible de créer un compte à un autre client");

    }

    @DeleteMapping(value = "/{compteIban}")
    @Transactional
    public ResponseEntity<?> deleteCompte(@PathVariable("clientId") String clientId, @PathVariable("compteIban") String compteIban, @AuthenticationPrincipal String clientEmail) {
        Client client = clientResource.findById(clientId).get();
        if(client.getEmail().equals(clientEmail)){
            Optional<Compte> compte = cr.findByClientAndIban(client, compteIban);
            compte.get().setClient(null);
            if (compte.isPresent()) {
                cr.delete(compte.get());
            }
            return ResponseEntity.noContent().build();
        }
        throw new RuntimeException("Impossible de supprimer un compte ne vous appartenant pas");

    }

}
