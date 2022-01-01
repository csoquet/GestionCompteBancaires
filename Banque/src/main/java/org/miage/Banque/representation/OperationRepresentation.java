package org.miage.Banque.representation;

import org.miage.Banque.assembler.OperationAssembler;
import org.miage.Banque.entity.Operation;
import org.miage.Banque.input.OperationInput;
import org.miage.Banque.resource.OperationResource;
import org.miage.Banque.validator.OperationValidator;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value="/operations", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Operation.class)
public class OperationRepresentation {

    private final OperationResource or;
    private final OperationAssembler oa;
    private final OperationValidator ov;

    public OperationRepresentation(OperationResource or, OperationAssembler oa, OperationValidator ov) {
        this.or = or;
        this.oa = oa;
        this.ov = ov;
    }

    @GetMapping
    public ResponseEntity<?> getAllOperations() {
        return ResponseEntity.ok(oa.toCollectionModel(or.findAll()));
    }

    @GetMapping(value = "/{operationId}")
    public ResponseEntity<?> getOneOperation(@PathVariable("operationId") String id) {
        return Optional.ofNullable(or.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(oa.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveOperation(@RequestBody @Valid OperationInput operation) {
        Operation operationSave = new Operation(
                UUID.randomUUID().toString(),
                operation.getDateheure(),
                operation.getLibelle(),
                operation.getMontant(),
                operation.getTauxapplique(),
                operation.getCategorie(),
                operation.getPays(),
                operation.getCompte()

        );
        Operation saved = or.save(operationSave);
        URI location = linkTo(OperationRepresentation.class).slash(saved.getIdoperation()).toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(value = "/{operationId}")
    @Transactional
    public ResponseEntity<?> deleteOperation(@PathVariable("operationId") String operationId) {
        Optional<Operation> operation = or.findById(operationId);
        if (operation.isPresent()) {
            or.delete(operation.get());
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{operationId}")
    @Transactional
    public ResponseEntity<?> updateOperation(@RequestBody Operation operation,
                                          @PathVariable("operationId") String operationId) {
        Optional<Operation> body = Optional.ofNullable(operation);
        if (!body.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        if (!or.existsById(operationId)) {
            return ResponseEntity.notFound().build();
        }
        operation.setIdoperation(operationId);
        or.save(operation);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{operationId}")
    @Transactional
    public ResponseEntity<?> updateOperationPartiel(@PathVariable("operationId") String operationId,
                                                 @RequestBody Map<Object, Object> fields) {
        Optional<Operation> body = or.findById(operationId);
        if (body.isPresent()) {
            Operation operation = body.get();
            fields.forEach((f, v) -> {
                Field field = ReflectionUtils.findField(Operation.class, f.toString());
                field.setAccessible(true);
                ReflectionUtils.setField(field, operation, v);
            });
            ov.validate(new OperationInput(
                    operation.getDateheure(),
                    operation.getLibelle(),
                    operation.getMontant(),
                    operation.getTauxapplique(),
                    operation.getCategorie(),
                    operation.getPays(),
                    operation.getCompte()
            ));
            operation.setIdoperation(operationId);
            or.save(operation);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
