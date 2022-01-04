package org.miage.Banque.representation;

import org.miage.Banque.assembler.OperationAssembler;
import org.miage.Banque.entity.CarteBancaire;
import org.miage.Banque.entity.Client;
import org.miage.Banque.entity.Compte;
import org.miage.Banque.entity.Operation;
import org.miage.Banque.input.OperationInput;
import org.miage.Banque.resource.CarteBancaireResource;
import org.miage.Banque.resource.CompteResource;
import org.miage.Banque.resource.OperationResource;
import org.miage.Banque.validator.OperationValidator;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value="/operations", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Operation.class)
public class OperationRepresentation {

    private final CompteResource cr;
    private final CarteBancaireResource carteResource;
    private final OperationResource or;
    private final OperationAssembler oa;
    private final OperationValidator ov;

    public OperationRepresentation(CompteResource cr, CarteBancaireResource carteResource, OperationResource or, OperationAssembler oa, OperationValidator ov) {
        this.cr = cr;
        this.carteResource = carteResource;
        this.or = or;
        this.oa = oa;
        this.ov = ov;
    }

    @GetMapping
    public ResponseEntity<?> getAllOperations() {
        return ResponseEntity.ok(oa.toCollectionModel(or.findAll()));
    }

    @GetMapping(value = "/compte/{compteId}")
    public ResponseEntity<?> getAllOperationByIdCompte(@PathVariable("compteId") String compteId) {
        Optional<Compte> compte = cr.findById(compteId);
        Iterable<Operation> operations =  or.findAllByComptedebiteur(compte);
        return ResponseEntity.ok(oa.toCollectionModel(operations));
    }

    @GetMapping(value = "/{operationId}")
    public ResponseEntity<?> getOneOperation(@PathVariable("operationId") String id) {
        return Optional.ofNullable(or.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(oa.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/{comptedebiteurId}/{comptecrediteurId}/{carteId}")
    @Transactional
    public ResponseEntity<?> saveOperation(@PathVariable("comptedebiteurId") String comptedebiteurId, @PathVariable("comptecrediteurId") String comptecrediteurId , @PathVariable("carteId") String carteId, @RequestBody @Valid OperationInput operation) {

        Compte comptedebiteur = cr.findById(comptedebiteurId).get();
        Compte comptecrediteur = cr.findById(comptecrediteurId).get();
        CarteBancaire carte = carteResource.findById(carteId).get();
        if(carte.getBloque()){
            return ResponseEntity.internalServerError().build();
        }

        Operation operationSave;
        Double montant = operation.getMontant();
        if(!comptedebiteur.getClient().getPays().equals(operation.getPays())) { //Si le paiement n'a pas lieu dans le même pays que le compte
            montant = operation.getMontant() *  operation.getTauxapplique();
        }
        comptedebiteur.setSolde(comptedebiteur.getSolde() - montant);
        comptecrediteur.setSolde(comptecrediteur.getSolde() + montant);
        operationSave = new Operation(
                UUID.randomUUID().toString(),
                new Timestamp(System.currentTimeMillis()),
                operation.getLibelle(),
                montant,
                operation.getTauxapplique(),
                operation.getCategorie(),
                operation.getPays(),
                comptedebiteur,
                comptecrediteur,
                carte
        );

        Operation saved = or.save(operationSave);
        URI location = linkTo(OperationRepresentation.class).slash(saved.getIdoperation()).toUri();
        return ResponseEntity.created(location).build();
    }

}
