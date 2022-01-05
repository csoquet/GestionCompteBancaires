package org.miage.Banque.assembler;

import org.miage.Banque.entity.Compte;
import org.miage.Banque.entity.Operation;
import org.miage.Banque.representation.CarteBancaireRepresentation;
import org.miage.Banque.representation.ClientRepresentation;
import org.miage.Banque.representation.CompteRepresentation;
import org.miage.Banque.representation.OperationRepresentation;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CompteAssembler implements RepresentationModelAssembler<Compte, EntityModel<Compte>> {

    @Override
    public EntityModel<Compte> toModel(Compte compte) {

        String clientId = compte.getClient().getIdclient();
        return EntityModel.of(compte,
                linkTo(methodOn(CompteRepresentation.class)
                        .getOneCompte(clientId, compte.getIban())).withSelfRel(),
                linkTo(methodOn(CompteRepresentation.class)
                        .getAllComptesByIdClient(clientId)).withRel("collection"),
                linkTo(methodOn(OperationRepresentation.class)
                        .getAllOperationByIdCompte(compte.getClient().getIdclient(), compte.getIban())).withRel("operations"),
                linkTo(methodOn(CarteBancaireRepresentation.class)
                        .getAllCarteByIdCompte(clientId, compte.getIban())).withRel("cartes")
        );

    }

    public CollectionModel<EntityModel<Compte>> toCollectionModel(Iterable<? extends Compte> entities) {
        List<EntityModel<Compte>> compteModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(i -> toModel(i))
                .collect(Collectors.toList());
        return CollectionModel.of(compteModel,
                linkTo(methodOn(CompteRepresentation.class)
                        .getAllComptesByIdClient(compteModel.get(0).getContent().getClient().getIdclient())).withSelfRel());
    }
}
