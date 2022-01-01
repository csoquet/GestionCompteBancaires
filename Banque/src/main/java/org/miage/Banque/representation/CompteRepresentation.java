package org.miage.Banque.representation;

import org.miage.Banque.assembler.CompteAssembler;
import org.miage.Banque.entity.Compte;
import org.miage.Banque.input.CompteInput;
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
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value="/comptes", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Compte.class)
public class CompteRepresentation {

    private final CompteResource cr;
    private final CompteAssembler ca;
    private final CompteValidator cv;

    public CompteRepresentation(CompteResource cr, CompteAssembler ca, CompteValidator cv) {
        this.cr = cr;
        this.ca = ca;
        this.cv = cv;
    }

    @GetMapping
    public ResponseEntity<?> getAllComptes() {
        return ResponseEntity.ok(ca.toCollectionModel(cr.findAll()));
    }

    @GetMapping(value = "/{compteId}")
    public ResponseEntity<?> getOneCompte(@PathVariable("compteId") String id) {
        return Optional.ofNullable(cr.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(ca.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveCompte(@RequestBody @Valid CompteInput compte) {
        Compte compteSave = new Compte(
                UUID.randomUUID().toString(),
                compte.getIban(),
                compte.getSolde(),
                compte.getClient(),
                compte.getOperation(),
                compte.getCartes()
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

    @PatchMapping(value = "/{compteId}")
    @Transactional
    public ResponseEntity<?> updateComptePartiel(@PathVariable("compteId") String compteId,
                                                 @RequestBody Map<Object, Object> fields) {
        Optional<Compte> body = cr.findById(compteId);
        if (body.isPresent()) {
            Compte compte = body.get();
            fields.forEach((f, v) -> {
                Field field = ReflectionUtils.findField(Compte.class, f.toString());
                field.setAccessible(true);
                ReflectionUtils.setField(field, compte, v);
            });
            cv.validate(new CompteInput(
                    compte.getIban(),
                    compte.getSolde(),
                    compte.getClient(),
                    compte.getOperation(),
                    compte.getCartes()
            ));
            compte.setIdcompte(compteId);
            cr.save(compte);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
