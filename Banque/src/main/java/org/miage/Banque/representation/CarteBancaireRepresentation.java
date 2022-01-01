package org.miage.Banque.representation;

import org.miage.Banque.assembler.CarteBancaireAssembler;
import org.miage.Banque.assembler.ClientAssembler;
import org.miage.Banque.entity.CarteBancaire;
import org.miage.Banque.entity.Client;
import org.miage.Banque.input.CarteBancaireInput;
import org.miage.Banque.input.ClientInput;
import org.miage.Banque.resource.CarteBancaireResource;
import org.miage.Banque.resource.ClientResource;
import org.miage.Banque.validator.CarteBancaireValidator;
import org.miage.Banque.validator.ClientValidator;
import org.springframework.util.ReflectionUtils;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import javax.validation.Valid;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value="/cartebancaires", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(CarteBancaire.class)
public class CarteBancaireRepresentation {

    private final CarteBancaireResource cbr;
    private final CarteBancaireAssembler cba;
    private final CarteBancaireValidator cbv;

    public CarteBancaireRepresentation(CarteBancaireResource cbr, CarteBancaireAssembler cba, CarteBancaireValidator cbv) {
        this.cbr = cbr;
        this.cba = cba;
        this.cbv = cbv;
    }

    @GetMapping
    public ResponseEntity<?> getAllCarteBancaire() {
        return ResponseEntity.ok(cba.toCollectionModel(cbr.findAll()));
    }

    @GetMapping(value = "/{cartebancaireId}")
    public ResponseEntity<?> getOneCarteBancaire(@PathVariable("cartebancaireId") String id) {
        return Optional.ofNullable(cbr.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(cba.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveCarteBancaire (@RequestBody @Valid CarteBancaireInput cb) {
        CarteBancaire cbSave = new CarteBancaire(
                UUID.randomUUID().toString(),
                cb.getNumcarte(),
                cb.getCode(),
                cb.getCrypto(),
                cb.getBloque(),
                cb.getLocalisation(),
                cb.getPlafond(),
                cb.getSanscontact(),
                cb.getVirtuelle(),
                cb.getCompte()
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

    @PatchMapping(value = "/{cartebancaireId}")
    @Transactional
    public ResponseEntity<?> updateCarteBancairePartiel(@PathVariable("cartebancaireId") String cartebancaireId,
                                                 @RequestBody Map<Object, Object> fields) {
        Optional<CarteBancaire> body = cbr.findById(cartebancaireId);
        if (body.isPresent()) {
            CarteBancaire cb = body.get();
            fields.forEach((f, v) -> {
                Field field = ReflectionUtils.findField(CarteBancaire.class, f.toString());
                field.setAccessible(true);
                ReflectionUtils.setField(field, cb, v);
            });
            cbv.validate(new CarteBancaireInput(
                    cb.getNumcarte(),
                    cb.getCode(),
                    cb.getCrypto(),
                    cb.getBloque(),
                    cb.getLocalisation(),
                    cb.getPlafond(),
                    cb.getSanscontact(),
                    cb.getVirtuelle(),
                    cb.getCompte()
            ));
            cb.setIdcarte(cartebancaireId);
            cbr.save(cb);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
