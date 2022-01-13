package org.miage.Banque.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.miage.Banque.entity.Client;
import org.miage.Banque.representation.ClientRepresentation;
import org.miage.Banque.representation.CompteRepresentation;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ClientAssembler implements RepresentationModelAssembler<Client, EntityModel<Client>> {

    @Override
    public EntityModel<Client> toModel(Client client) {
        /*EntityModel<Client> clientModel = EntityModel.of(client);
        clientModel.add(linkTo(methodOn(ClientsController.class).getOne(client.getId())).withSelfRel());
        if(client.getCompte() != null) {
            clientModel.add(linkTo(methodOn(ComptesController.class).getOne(client.getCompte().getId())).withRel("compte"));
        }
        return clientModel;*/
        return EntityModel.of(client,
                linkTo(methodOn(ClientRepresentation.class)
                        .getOneClient(client.getIdclient())).withSelfRel(),
                linkTo(methodOn(ClientRepresentation.class)
                        .getAllClients()).withRel("collection"),
                linkTo(methodOn(CompteRepresentation.class).getAllComptesByIdClient(client.getIdclient())).withRel("comptes")
    );

    }

    public CollectionModel<EntityModel<Client>> toCollectionModel(Iterable<? extends Client> entities) {
        List<EntityModel<Client>> clientModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(i -> toModel(i))
                .collect(Collectors.toList());
        return CollectionModel.of(clientModel,
                linkTo(methodOn(ClientRepresentation.class)
                        .getAllClients()).withSelfRel());
    }
}
