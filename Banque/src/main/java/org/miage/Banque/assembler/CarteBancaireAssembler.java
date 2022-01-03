package org.miage.Banque.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.miage.Banque.entity.CarteBancaire;
import org.miage.Banque.entity.Client;
import org.miage.Banque.representation.CarteBancaireRepresentation;
import org.miage.Banque.representation.ClientRepresentation;
import org.miage.Banque.representation.CompteRepresentation;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class CarteBancaireAssembler implements RepresentationModelAssembler<CarteBancaire, EntityModel<CarteBancaire>>{

    @Override
    public EntityModel<CarteBancaire> toModel(CarteBancaire cartebancaire) {
        return EntityModel.of(cartebancaire,
                linkTo(methodOn(CarteBancaireRepresentation.class)
                        .getOneCarteBancaire(cartebancaire.getIdcarte())).withSelfRel(),
                linkTo(methodOn(CarteBancaireRepresentation.class)
                        .getAllCarteBancaire()).withRel("collection"),
                linkTo(methodOn(CompteRepresentation.class)
                        .getOneCompte(cartebancaire.getCompte().getIdcompte())).withRel("comptes")
        );
    }

    public CollectionModel<EntityModel<CarteBancaire>> toCollectionModel(Iterable<? extends CarteBancaire> entities) {
        List<EntityModel<CarteBancaire>> cartebancaireModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(i -> toModel(i))
                .collect(Collectors.toList());
        return CollectionModel.of(cartebancaireModel,
                linkTo(methodOn(CarteBancaireRepresentation.class)
                        .getAllCarteBancaire()).withSelfRel());
    }
}
