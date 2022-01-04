package org.miage.Banque.representation;

import org.miage.Banque.assembler.CarteBancaireAssembler;
import org.miage.Banque.entity.CarteBancaire;
import org.miage.Banque.entity.Compte;
import org.miage.Banque.entity.Operation;
import org.miage.Banque.input.CarteBancaireInput;
import org.miage.Banque.resource.CarteBancaireResource;
import org.miage.Banque.resource.CompteResource;
import org.miage.Banque.validator.CarteBancaireValidator;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value="/cartebancaires", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(CarteBancaire.class)
public class CarteBancaireRepresentation {

    private final CompteResource cr;
    private final CarteBancaireResource cbr;
    private final CarteBancaireAssembler cba;
    private final CarteBancaireValidator cbv;

    public CarteBancaireRepresentation(CompteResource cr, CarteBancaireResource cbr, CarteBancaireAssembler cba, CarteBancaireValidator cbv) {
        this.cr = cr;
        this.cbr = cbr;
        this.cba = cba;
        this.cbv = cbv;
    }

    @GetMapping
    public ResponseEntity<?> getAllCarteBancaire() {
        return ResponseEntity.ok(cba.toCollectionModel(cbr.findAll()));
    }

    @GetMapping(value = "/compte/{compteId}")
    public ResponseEntity<?> getAllCarteByIdCompte(@PathVariable("compteId") String compteId) {
        Optional<Compte> compte = cr.findById(compteId);
        Iterable<CarteBancaire> cb =  cbr.findAllByCompte(compte);
        return ResponseEntity.ok(cba.toCollectionModel(cb));
    }

    @GetMapping(value = "/{cartebancaireId}")
    public ResponseEntity<?> getOneCarteBancaire(@PathVariable("cartebancaireId") String id) {
        return Optional.ofNullable(cbr.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(cba.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/{compteId}")
    @Transactional
    public ResponseEntity<?> saveCarteBancaire (@PathVariable("compteId") String compteId, @RequestBody @Valid CarteBancaireInput cb) {

        Compte compte = cr.findById(compteId).get();
        String numero = "";
        String code= "";
        String crypto= "";
        int Min = 1;
        int Max = 9;
        for(int i = 1; i <= 16; i++){//Générer des chiffres du numéro de carte
            int nombreAleatoire = Min + (int)(Math.random() * (Max - Min) + 1);
            numero += nombreAleatoire;
        }

        for(int i = 1; i <= 4; i++){//Générer des chiffres du code
            int nombreAleatoire = Min + (int)(Math.random() * (Max - Min) + 1);
            code += nombreAleatoire;
        }

        for(int i = 1; i <= 3; i++){//Générer des chiffres du cryptogramme
            int nombreAleatoire = Min + (int)(Math.random() * (Max - Min) + 1);
            crypto += nombreAleatoire;
        }

        CarteBancaire cbSave = new CarteBancaire(
                UUID.randomUUID().toString(),
                numero,
                code,
                crypto,
                cb.getBloque(),
                cb.getLocalisation(),
                cb.getPlafond(),
                cb.getSanscontact(),
                cb.getVirtuelle(),
                compte
        );
        CarteBancaire saved = cbr.save(cbSave);
        URI location = linkTo(ClientRepresentation.class).slash(saved.getIdcarte()).toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(value = "/{cartebancaireId}")
    @Transactional
    public ResponseEntity<?> deleteCarteBancaire(@PathVariable("cartebancaireId") String cartebancaireId) {
        Optional<CarteBancaire> cb = cbr.findById(cartebancaireId);
        if (cb.isPresent()) {
            cbr.delete(cb.get());
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{cartebancaireId}")
    @Transactional
    public ResponseEntity<?> updateCarteBancaire(@RequestBody CarteBancaire cb,
                                          @PathVariable("cartebancaireId") String cartebancaireId) {
        Optional<CarteBancaire> body = Optional.ofNullable(cb);
        if (!body.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        if (!cbr.existsById(cartebancaireId)) {
            return ResponseEntity.notFound().build();
        }
        cb.setIdcarte(cartebancaireId);
        cbr.save(cb);
        return ResponseEntity.ok().build();
    }

}
