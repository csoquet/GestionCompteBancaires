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

        String clientId = operation.getComptedebiteur().getClient().getIdclient();
        String compteId = operation.getComptedebiteur().getIban();

        return EntityModel.of(operation,
                linkTo(methodOn(OperationRepresentation.class)
                        .getOneOperation(clientId, compteId, operation.getIdoperation())).withSelfRel(),
                linkTo(methodOn(OperationRepresentation.class)
                        .getAllOperationByIdCompte(clientId, compteId)).withRel("collection"),
                linkTo(methodOn(CompteRepresentation.class)
                        .getOneCompte(clientId, compteId)).withRel("comptes"),
                linkTo(methodOn(CarteBancaireRepresentation.class)
                        .getOneCarteBancaire(clientId, compteId, operation.getCarte().getNumcarte())).withRel("carte")
        );
    }

    public CollectionModel<EntityModel<Operation>> toCollectionModel(Iterable<? extends Operation> entities) {
        List<EntityModel<Operation>> operationModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(i -> toModel(i))
                .collect(Collectors.toList());
        String clientId = operationModel.get(0).getContent().getComptedebiteur().getClient().getIdclient();
        String compteId = operationModel.get(0).getContent().getComptedebiteur().getIban();
        return CollectionModel.of(operationModel,
                linkTo(methodOn(OperationRepresentation.class)
                        .getAllOperationByIdCompte(clientId, compteId)).withSelfRel());
    }
}
