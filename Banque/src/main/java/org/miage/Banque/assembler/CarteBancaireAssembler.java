package org.miage.Banque.assembler;

import lombok.SneakyThrows;
import org.miage.Banque.entity.CarteBancaire;
import org.miage.Banque.representation.CarteBancaireRepresentation;
import org.miage.Banque.representation.CompteRepresentation;
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
public class CarteBancaireAssembler implements RepresentationModelAssembler<CarteBancaire, EntityModel<CarteBancaire>>{

    @Override
    public EntityModel<CarteBancaire> toModel(CarteBancaire cartebancaire) {
        String clientId = cartebancaire.getCompte().getClient().getIdclient();
        String compteId = cartebancaire.getCompte().getIban();
        return EntityModel.of(cartebancaire,
                linkTo(methodOn(CarteBancaireRepresentation.class)
                        .getOneCarteBancaire(clientId, compteId, cartebancaire.getNumcarte())).withSelfRel(),
                linkTo(methodOn(CarteBancaireRepresentation.class)
                        .getAllCarteByIdCompte(clientId, compteId)).withRel("collection"),
                linkTo(methodOn(CompteRepresentation.class)
                        .getOneCompte(clientId, compteId)).withRel("comptes")
        );
    }

    @SneakyThrows
    public CollectionModel<EntityModel<CarteBancaire>> toCollectionModel(Iterable<? extends CarteBancaire> entities) {
        List<EntityModel<CarteBancaire>> cartebancaireModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(i -> toModel(i))
                .collect(Collectors.toList());

        try{
            CollectionModel collectionModel = CollectionModel.of(cartebancaireModel);
            if (cartebancaireModel.size() > 0) {
                String clientId = cartebancaireModel.get(0).getContent().getCompte().getClient().getIdclient();
                String compteId = cartebancaireModel.get(0).getContent().getCompte().getIban();
                collectionModel.add(linkTo(methodOn(CarteBancaireRepresentation.class).getAllCarteByIdCompte(clientId, compteId)).withSelfRel());
            }
            return collectionModel;
        } catch (Exception e){
        throw new Exception("Pas de cartes pour ce compte");
    }


    }
}
