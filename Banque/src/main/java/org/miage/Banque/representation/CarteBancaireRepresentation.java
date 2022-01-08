package org.miage.Banque.representation;

import org.miage.Banque.assembler.CarteBancaireAssembler;
import org.miage.Banque.entity.CarteBancaire;
import org.miage.Banque.entity.Compte;
import org.miage.Banque.input.CarteBancaireInput;
import org.miage.Banque.resource.CarteBancaireResource;
import org.miage.Banque.resource.CompteResource;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value="clients/{clientId}/comptes/{compteIban}/cartebancaires", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(CarteBancaire.class)
public class CarteBancaireRepresentation {

    private final CompteResource cr;
    private final CarteBancaireResource cbr;
    private final CarteBancaireAssembler cba;

    public CarteBancaireRepresentation(CompteResource cr, CarteBancaireResource cbr, CarteBancaireAssembler cba) {
        this.cr = cr;
        this.cbr = cbr;
        this.cba = cba;
    }


    @GetMapping
    public ResponseEntity<?> getAllCarteByIdCompte(@PathVariable("clientId") String clientId,
                                                   @PathVariable("compteIban") String compteIban) {
        Optional<Compte> compte = cr.findById(compteIban);
        Iterable<CarteBancaire> cb =  cbr.findAllByCompteAndSupprimerFalse(compte);
        return ResponseEntity.ok(cba.toCollectionModel(cb));
    }

    @GetMapping(value = "/{cartebancaireNum}")
    public ResponseEntity<?> getOneCarteBancaire(@PathVariable("clientId") String clientId,
                                                 @PathVariable("compteIban") String compteIban,
                                                 @PathVariable("cartebancaireNum") String id) {
        return Optional.ofNullable(cbr.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(cba.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveCarteBancaire (@PathVariable("clientId") String clientId,
                                                @PathVariable("compteIban") String compteIban,
                                                @RequestBody @Valid CarteBancaireInput cb) {

        Compte compte = cr.findById(compteIban).get();
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

        SimpleDateFormat dtf = new SimpleDateFormat("dd-MM-yyyy"); //On initialise le format de date final souhaité
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, 2); //On ajoute 2 année à la date d'expiration si la carte n'est pas virtuelle
        date = c.getTime();
        String expiration = dtf.format(date); //On met en forme la date

        if(cb.getVirtuelle()){ // Si la carte est virtuelle alors
            Date date2 = new Date();
            Calendar c2 = Calendar.getInstance();
            c2.setTime(date2);
            c2.add(Calendar.DATE, 15); //On ajoute seulement 15 jours à la date d'experation
            date2 = c2.getTime();
            expiration = dtf.format(date2); //On met en forme la date
        }
        CarteBancaire cbSave = new CarteBancaire(
                numero,
                code,
                crypto,
                false,
                cb.getLocalisation(),
                cb.getPlafond(),
                cb.getSanscontact(),
                cb.getVirtuelle(),
                expiration,
                false,
                compte
        );
        CarteBancaire saved = cbr.save(cbSave);
        URI location = linkTo(methodOn(CarteBancaireRepresentation.class).getOneCarteBancaire(clientId, compteIban, saved.getNumcarte())).toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(value = "/{cartebancaireNum}")
    @Transactional
    public ResponseEntity<?> deleteCarteBancaire(@PathVariable("clientId") String clientId,
                                                 @PathVariable("compteIban") String compteIban,
                                                 @PathVariable("cartebancaireNum") String cartebancaireNum) {
        Optional<CarteBancaire> cb = cbr.findById(cartebancaireNum);
        if (cb.isPresent()) {
            cb.get().setSupprimer(true);
            cbr.save(cb.get());
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{cartebancaireNum}")
    @Transactional
    public ResponseEntity<?> updateCarteBancaire(@PathVariable("clientId") String clientId,
                                                 @PathVariable("compteIban") String compteIban,
                                                 @PathVariable("cartebancaireNum") String cartebancaireNum,
                                                 @RequestBody CarteBancaire cb) {
        Compte compte = cr.findById(compteIban).get();
        Optional<CarteBancaire> body = Optional.ofNullable(cb);
        if (!body.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        if (!cbr.existsById(cartebancaireNum)) {
            return ResponseEntity.notFound().build();
        }
        cb.setCompte(compte);
        cb.setNumcarte(cartebancaireNum);
        cbr.save(cb);
        return ResponseEntity.ok().build();
    }

}
