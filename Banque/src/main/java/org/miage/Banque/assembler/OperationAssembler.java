package org.miage.Banque.assembler;

import org.miage.Banque.entity.Operation;
import org.miage.Banque.representation.CarteBancaireRepresentation;
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
public class OperationAssembler implements RepresentationModelAssembler<Operation, EntityModel<Operation>> {

    @Override
    public EntityModel<Operation> toModel(Operation operation) {
        return EntityModel.of(operation,
                linkTo(methodOn(OperationRepresentation.class)
                        .getOneOperation(operation.getIdoperation())).withSelfRel(),
                linkTo(methodOn(OperationRepresentation.class)
                        .getAllOperations()).withRel("collection"),
                linkTo(methodOn(CompteRepresentation.class)
                        .getOneCompte(operation.getComptedebiteur().getIdcompte())).withRel("comptes"),
                linkTo(methodOn(CompteRepresentation.class)
                        .getOneCompte(operation.getComptecrediteur().getIdcompte())).withRel("comptes"),
                linkTo(methodOn(CarteBancaireRepresentation.class)
                        .getOneCarteBancaire(operation.getCarte().getIdcarte())).withRel("carte")
        );
    }

    public CollectionModel<EntityModel<Operation>> toCollectionModel(Iterable<? extends Operation> entities) {
        List<EntityModel<Operation>> operationModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(i -> toModel(i))
                .collect(Collectors.toList());
        return CollectionModel.of(operationModel,
                linkTo(methodOn(OperationRepresentation.class)
                        .getAllOperations()).withSelfRel());
    }
}
