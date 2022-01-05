package org.miage.Banque.representation;

import org.miage.Banque.assembler.OperationAssembler;
import org.miage.Banque.entity.CarteBancaire;
import org.miage.Banque.entity.Compte;
import org.miage.Banque.entity.Operation;
import org.miage.Banque.input.OperationInput;
import org.miage.Banque.resource.CarteBancaireResource;
import org.miage.Banque.resource.CompteResource;
import org.miage.Banque.resource.OperationResource;
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
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value="/clients/{clientId}/comptes/{compteIban}/operations", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Operation.class)
public class OperationRepresentation {

    private final CompteResource cr;
    private final CarteBancaireResource carteResource;
    private final OperationResource or;
    private final OperationAssembler oa;

    public OperationRepresentation(CompteResource cr, CarteBancaireResource carteResource, OperationResource or, OperationAssembler oa) {
        this.cr = cr;
        this.carteResource = carteResource;
        this.or = or;
        this.oa = oa;
    }


    @GetMapping
    public ResponseEntity<?> getAllOperationByIdCompte(@PathVariable("clientId") String clientId,
                                                             @PathVariable("compteIban") String compteIban) {
        Optional<Compte> compte = cr.findById(compteIban);
        Iterable<Operation> operations =  or.findAllByComptedebiteur(compte);
        return ResponseEntity.ok(oa.toCollectionModel(operations));
    }

    @GetMapping(value = "/{operationId}")
    public ResponseEntity<?> getOneOperation(@PathVariable("clientId") String clientId,
                                             @PathVariable("compteIban") String compteIban,
                                             @PathVariable("operationId") String id) {
        return Optional.ofNullable(or.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(oa.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveOperation(@PathVariable("clientId") String clientId,
                                           @PathVariable("compteIban") String compteIban,
                                           @RequestBody @Valid OperationInput operation) {

        Compte comptedebiteur = cr.findByIban(compteIban);
        Compte comptecrediteur = cr.findByIban(operation.getComptecrediteurIban());
        CarteBancaire carte = carteResource.findByNumcarte(operation.getCarteNumero());
        if(carte.getBloque() || carte.getSupprimer()){ //Si la carte est bloquée ou supprimer alors on ne peut pas l'utiliser
            return ResponseEntity.badRequest().build();
        }
        if(carte.getVirtuelle()){ //Si c'est une carte virtuelle alors elle est supprimer après l'utilisation
            carte.setSupprimer(true);
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
        URI location = linkTo(methodOn(OperationRepresentation.class).getOneOperation(clientId, compteIban, saved.getIdoperation())).toUri();
        return ResponseEntity.created(location).build();
    }

}
