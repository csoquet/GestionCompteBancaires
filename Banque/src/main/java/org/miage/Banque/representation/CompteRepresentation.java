package org.miage.Banque.representation;

import org.miage.Banque.assembler.CompteAssembler;
import org.miage.Banque.entity.Client;
import org.miage.Banque.entity.Compte;
import org.miage.Banque.input.CompteInput;
import org.miage.Banque.resource.ClientResource;
import org.miage.Banque.resource.CompteResource;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getAllComptesByIdClient(@PathVariable("clientId") String clientId) {
        Optional<Client> client = clientResource.findById(clientId);
        Iterable<Compte> comptes =  cr.findAllByClient(client);
        return ResponseEntity.ok(ca.toCollectionModel(comptes));
    }

    @GetMapping(value = "/{compteIban}")
    public ResponseEntity<?> getOneCompte(@PathVariable("clientId") String clientId, @PathVariable("compteIban") String id) {
        return Optional.ofNullable(cr.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(ca.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveCompte(@PathVariable("clientId") String clientId, @RequestBody @Valid CompteInput compte) {

        Optional<Client> client = clientResource.findById(clientId);
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

    @DeleteMapping(value = "/{compteIban}")
    @Transactional
    public ResponseEntity<?> deleteCompte(@PathVariable("clientId") String clientId, @PathVariable("compteIban") String compteIban) {
        Optional<Compte> compte = cr.findById(compteIban);
        compte.get().setClient(null);
        if (compte.isPresent()) {
            cr.delete(compte.get());
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{compteIban}")
    @Transactional
    public ResponseEntity<?> updateCompte(@RequestBody Compte compte,
                                          @PathVariable("compteIban") String compteIban,
                                          @PathVariable("clientId") String clientId) {
        Client client = clientResource.findById(clientId).get();
        Optional<Compte> body = Optional.ofNullable(compte);
        if (!body.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        if (!cr.existsById(compteIban)) {
            return ResponseEntity.notFound().build();
        }
        compte.setClient(client);
        compte.setIban(compteIban);
        cr.save(compte);
        return ResponseEntity.ok().build();
    }

}
