package org.miage.Banque.representation;

import org.miage.Banque.assembler.OperationAssembler;
import org.miage.Banque.entity.*;
import org.miage.Banque.input.OperationInput;
import org.miage.Banque.resource.CarteBancaireResource;
import org.miage.Banque.resource.ClientResource;
import org.miage.Banque.resource.CompteResource;
import org.miage.Banque.resource.OperationResource;
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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value="/clients/{clientId}/comptes/{compteIban}/operations", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Operation.class)
public class OperationRepresentation {

    private final ClientResource clientR;
    private final CompteResource cr;
    private final CarteBancaireResource carteResource;
    private final OperationResource or;
    private final OperationAssembler oa;

    public OperationRepresentation(ClientResource clientR, CompteResource cr, CarteBancaireResource carteResource, OperationResource or, OperationAssembler oa) {
        this.clientR = clientR;
        this.cr = cr;
        this.carteResource = carteResource;
        this.or = or;
        this.oa = oa;
    }


    @GetMapping
    //@PostAuthorize("returnObject.content.client.email == authentication.name or hasRole('ROLE_ADMIN')")
    public CollectionModel<EntityModel<Operation>> getAllOperationByIdCompte(@PathVariable("clientId") String clientId,
                                                                             @PathVariable("compteIban") String compteIban) {
        Client client = clientR.findById(clientId).get();
        Optional<Compte> compte = cr.findByClientAndIban(client, compteIban);
        Iterable<Operation> operations =  or.findAllByComptedebiteur(compte);
        return oa.toCollectionModel(operations);
    }

    @GetMapping(value = "/{operationId}")
    @PostAuthorize("returnObject.content.comptedebiteur.client.email == authentication.name or hasRole('ROLE_ADMIN')")
    public EntityModel getOneOperation(@PathVariable("clientId") String clientId,
                                             @PathVariable("compteIban") String compteIban,
                                             @PathVariable("operationId") String id) {

            Client client = clientR.findById(clientId).get();
            Optional<Compte> compte = cr.findByClientAndIban(client, compteIban);
            return oa.toModel(or.findByIdoperationAndComptedebiteur(id, compte.get()).get());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveOperation(@PathVariable("clientId") String clientId,
                                           @PathVariable("compteIban") String compteIban,
                                           @RequestBody @Valid OperationInput operation,
                                           @AuthenticationPrincipal String clientEmail) throws ParseException {

        Client client = clientR.getByIdclient(clientId);
        Client clientReel = clientR.findByEmail(clientEmail);
        boolean admin = false;
        for (Role role : clientReel.getRoles()) {
            if(role.getNom().equals("ROLE_ADMIN")){
                admin = true;
            }
        }
        if(client.getEmail().equals(clientEmail) || admin){
            Compte comptedebiteur = cr.findByClientAndIban(client, compteIban).get();
            Compte comptecrediteur =  cr.findByIban(operation.getComptecrediteurIban());
            CarteBancaire carte = carteResource.findByNumcarteAndCompte(operation.getCarteNumero(), comptedebiteur).get();

            /* Partie carte bloqué et carte supprimer */
            if(carte.getBloque() || carte.getSupprimer()){ //Si la carte est bloquée ou supprimer alors on ne peut pas l'utiliser
                return ResponseEntity.badRequest().build();
            }

            /* Partie de vérification du code */
            if(!carte.getSanscontact()){
                if(!carte.getCode().equals(operation.getCodeCarte())){
                    return ResponseEntity.badRequest().build();
                }
            }


            /* Partie Expiration de la carte*/
            LocalDate date = LocalDate.now(); //Calcul la date du jour
            Date dateExpiration = new SimpleDateFormat("dd-MM-yyyy").parse(carte.getExpiration()); //Calcul de la date d'expiration
            LocalDate dateE = dateExpiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); //Mise en forme de la date d'expiration
            if(date.isEqual(dateE) || date.isAfter(dateE)){ //Si la date d'expiration est aujourd'hui ou si elle est déjà passé
                carte.setSupprimer(true); //On supprime la carte
                return ResponseEntity.badRequest().build(); //Et on renvoie une erreur
            }

            /* Partie limite de plafond de la carte */
            LocalDate dateAvant = date.minusDays(30); //On calcule la date 30 jour avant la date d'aujourd'hui
            List<Operation> operationCarte = or.findAllByCarte(carte); //On récupère toutes les opérations de la carte bancaire
            Double montantTotal = operation.getMontant();
            for(Operation o : operationCarte){ //On parcours les opéraitons
                LocalDate dateOperation = o.getDateheure().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); //On convertis la date dans un format
                if(dateOperation.isAfter(dateAvant) && dateOperation.isBefore(date)){ //Si l'opération a eu lieu entre aujourd'hui et 30 jours avant
                    montantTotal += o.getMontant(); //Alors on enregistre le montant
                }
            }
            if(montantTotal >= carte.getPlafond()){ //Si le montant total dépasse le plafond
                return ResponseEntity.badRequest().build(); //Alors on renvoie une erreur
            }

            /* Partie carte virtuelle utilisé */
            if(carte.getVirtuelle()){ //Si c'est une carte virtuelle alors elle est supprimer après l'utilisation
                carte.setSupprimer(true);
            }

            Operation operationSave;

            /* Partie du taux de change */
            Double montant = operation.getMontant();
            if(!comptedebiteur.getClient().getPays().equals(operation.getPays())) { //Si le paiement n'a pas lieu dans le même pays que le compte
                montant = operation.getMontant() *  operation.getTauxapplique();
            }
            /* Partie du solde du compte */
            if(comptedebiteur.getSolde() < montant){ //Si le compte n'a pas assez de solde
                return ResponseEntity.badRequest().build(); //On envoie une erreur
            }
            comptedebiteur.setSolde(comptedebiteur.getSolde() - montant); //Sinon on débite le compte débiteur
            comptecrediteur.setSolde(comptecrediteur.getSolde() + montant); //Et on crédite le compte créditeur

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
        throw new RuntimeException("Vous ne pouvez pas créer une opération sur un compte qui ne vous appartient pas");

    }

}
