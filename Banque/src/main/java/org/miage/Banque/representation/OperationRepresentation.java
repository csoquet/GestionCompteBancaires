package org.miage.Banque.representation;

import lombok.extern.slf4j.Slf4j;
import org.miage.Banque.assembler.OperationAssembler;
import org.miage.Banque.entity.CarteBancaire;
import org.miage.Banque.entity.Client;
import org.miage.Banque.entity.Compte;
import org.miage.Banque.entity.Operation;
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
@Slf4j
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
    public CollectionModel<EntityModel<Operation>> getAllOperationByIdCompte(@PathVariable("clientId") String clientId,
                                                                             @PathVariable("compteIban") String compteIban) {
        Client client = clientR.findById(clientId).get();
        Optional<Compte> compte = cr.findByClientAndIban(client, compteIban);
        Iterable<Operation> operations = or.findAllByComptedebiteur(compte);
        return oa.toCollectionModel(operations);
    }

    @GetMapping(value = "/{operationId}")
    @PostAuthorize("returnObject.content.comptedebiteur.client.email == authentication.name or hasRole('ROLE_ADMIN')")
    public EntityModel getOneOperation(@PathVariable("clientId") String clientId,
                                       @PathVariable("compteIban") String compteIban,
                                       @PathVariable("operationId") String id) {
        Client client = clientR.findById(clientId).get();
        Compte compte = cr.findByClientAndIban(client, compteIban).get();
        Operation operation = or.findByIdoperationAndComptedebiteur(id, compte).get();
        return oa.toModel(operation);


    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveOperation(@PathVariable("clientId") String clientId,
                                           @PathVariable("compteIban") String compteIban,
                                           @RequestBody @Valid OperationInput operation) throws ParseException {

        Client client = clientR.getByIdclient(clientId);
            Compte comptedebiteur = cr.findByClientAndIban(client, compteIban).get();
            Compte comptecrediteur = cr.findByIban(operation.getComptecrediteurIban());
            Optional<CarteBancaire> carte = carteResource.findByNumcarteAndCompte(operation.getCarteNumero(), comptedebiteur);

            if(carte.isPresent()){
                /* Partie carte bloqu?? et carte supprimer */
                if (carte.get().getBloque() || carte.get().getSupprimer()) { //Si la carte est bloqu??e ou supprimer alors on ne peut pas l'utiliser
                    return ResponseEntity.badRequest().build();
                }

                /* Partie de v??rification du code */
                if (!carte.get().getSanscontact()) {
                    if (!carte.get().getCode().equals(operation.getCodeCarte())) {
                        return ResponseEntity.badRequest().build();
                    }
                }

                /* Partie Expiration de la carte*/
                LocalDate date = LocalDate.now(); //Calcul la date du jour
                Date dateExpiration = new SimpleDateFormat("dd-MM-yyyy").parse(carte.get().getExpiration()); //Calcul de la date d'expiration
                LocalDate dateE = dateExpiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); //Mise en forme de la date d'expiration
                if (date.isEqual(dateE) || date.isAfter(dateE)) { //Si la date d'expiration est aujourd'hui ou si elle est d??j?? pass??
                    carte.get().setSupprimer(true); //On supprime la carte
                    return ResponseEntity.badRequest().build(); //Et on renvoie une erreur
                }

                /* Partie limite de plafond de la carte */
                LocalDate dateAvant = date.minusDays(30); //On calcule la date 30 jour avant la date d'aujourd'hui
                List<Operation> operationCarte = or.findAllByCarte(carte.get()); //On r??cup??re toutes les op??rations de la carte bancaire
                Double montantTotal = operation.getMontant();
                for (Operation o : operationCarte) { //On parcours les op??raitons
                    LocalDate dateOperation = o.getDateheure().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); //On convertis la date dans un format
                    if (dateOperation.isAfter(dateAvant) && dateOperation.isBefore(date)) { //Si l'op??ration a eu lieu entre aujourd'hui et 30 jours avant
                        montantTotal += o.getMontant(); //Alors on enregistre le montant
                    }
                }
                if (montantTotal >= carte.get().getPlafond()) { //Si le montant total d??passe le plafond
                    return ResponseEntity.badRequest().build(); //Alors on renvoie une erreur
                }

                /* Partie carte virtuelle utilis?? */
                if (carte.get().getVirtuelle()) { //Si c'est une carte virtuelle alors elle est supprimer apr??s l'utilisation
                    carte.get().setSupprimer(true);
                }
            }

            Operation operationSave;

            /* Partie pas de taux appliqu?? */
            if(operation.getTauxapplique() == null){
                operation.setTauxapplique(1.0);
            }

            /* Partie du taux de change */
            Double montant = operation.getMontant();
            if (!comptedebiteur.getClient().getPays().equals(operation.getPays())) { //Si le paiement n'a pas lieu dans le m??me pays que le compte
                montant = operation.getMontant() * operation.getTauxapplique();
            }
            /* Partie du solde du compte */
           if (comptedebiteur.getSolde() < montant) { //Si le compte n'a pas assez de solde
                return ResponseEntity.badRequest().build(); //On envoie une erreur
           }
           comptedebiteur.setSolde(comptedebiteur.getSolde() - montant); //Sinon on d??bite le compte d??biteur
           comptecrediteur.setSolde(comptecrediteur.getSolde() + montant); //Et on cr??dite le compte cr??diteur



            if(carte.isPresent()){
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
                        carte.get()
                );
            }
            else{
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
                        null
                );
            }

                Operation saved = or.save(operationSave);
                URI location = linkTo(methodOn(OperationRepresentation.class).getOneOperation(clientId, compteIban, saved.getIdoperation())).toUri();
                return ResponseEntity.created(location).build();

        }
    }



