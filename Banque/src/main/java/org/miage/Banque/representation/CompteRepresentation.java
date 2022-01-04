package org.miage.Banque.representation;

import org.miage.Banque.assembler.CompteAssembler;
import org.miage.Banque.entity.Client;
import org.miage.Banque.entity.Compte;
import org.miage.Banque.input.CompteInput;
import org.miage.Banque.resource.ClientResource;
import org.miage.Banque.resource.CompteResource;
import org.miage.Banque.validator.CompteValidator;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value="/comptes", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Compte.class)
public class CompteRepresentation {

    private final ClientResource clientResource;
    private final CompteResource cr;
    private final CompteAssembler ca;
    private final CompteValidator cv;

    public CompteRepresentation(ClientResource clientResource, CompteResource cr, CompteAssembler ca, CompteValidator cv) {
        this.clientResource = clientResource;
        this.cr = cr;
        this.ca = ca;
        this.cv = cv;
    }

    @GetMapping
    public ResponseEntity<?> getAllComptes() {
        return ResponseEntity.ok(ca.toCollectionModel(cr.findAll()));
    }

    @GetMapping(value = "/client/{clientId}")
    public ResponseEntity<?> getAllComptesByIdClient(@PathVariable("clientId") String clientId) {
        Optional<Client> client = clientResource.findById(clientId);
        Iterable<Compte> comptes =  cr.findAllByClient(client);
        return ResponseEntity.ok(ca.toCollectionModel(comptes));
    }

    @GetMapping(value = "/{compteId}")
    public ResponseEntity<?> getOneCompte(@PathVariable("compteId") String id) {
        return Optional.ofNullable(cr.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(ca.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/{clientId}")
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
                UUID.randomUUID().toString(),
                iban,
                compte.getSolde(),
                client.get()
        );
        Compte saved = cr.save(compteSave);
        URI location = linkTo(CompteRepresentation.class).slash(saved.getIdcompte()).toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(value = "/{compteId}")
    @Transactional
    public ResponseEntity<?> deleteCompte(@PathVariable("compteId") String compteId) {
        Optional<Compte> compte = cr.findById(compteId);
        if (compte.isPresent()) {
            cr.delete(compte.get());
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{compteId}")
    @Transactional
    public ResponseEntity<?> updateCompte(@RequestBody Compte compte,
                                          @PathVariable("compteId") String compteId) {
        Optional<Compte> body = Optional.ofNullable(compte);
        if (!body.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        if (!cr.existsById(compteId)) {
            return ResponseEntity.notFound().build();
        }
        compte.setIdcompte(compteId);
        cr.save(compte);
        return ResponseEntity.ok().build();
    }

}
