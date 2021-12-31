package org.miage.Banque.representation;

import org.miage.Banque.assembler.ClientAssembler;
import org.miage.Banque.entity.Client;
import org.miage.Banque.input.ClientInput;
import org.miage.Banque.resource.ClientResource;
import org.miage.Banque.validator.ClientValidator;
import org.springframework.util.ReflectionUtils;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import javax.validation.Valid;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value="/clients", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Client.class)
public class ClientRepresentation {

    private final ClientResource cr;
    private final ClientAssembler ca;
    private final ClientValidator cv;

    public ClientRepresentation(ClientResource cr, ClientAssembler ca, ClientValidator cv) {
        this.cr = cr;
        this.ca = ca;
        this.cv = cv;
    }

    @GetMapping
    public ResponseEntity<?> getAllClients() {
        return ResponseEntity.ok(ca.toCollectionModel(cr.findAll()));
    }

    @GetMapping(value = "/{clientId}")
    public ResponseEntity<?> getOneClient(@PathVariable("clientId") String id) {
        return Optional.ofNullable(cr.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(ca.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveClient(@RequestBody @Valid ClientInput client) {
        Client clientSave = new Client(
                UUID.randomUUID().toString(),
                client.getNom(),
                client.getPrenom(),
                client.getSecret(),
                client.getDatenaiss(),
                client.getPays(),
                client.getNopasseport(),
                client.getNumtel(),
                client.getComptes()
        );
        Client saved = cr.save(clientSave);
        URI location = linkTo(ClientRepresentation.class).slash(saved.getIdclient()).toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(value = "/{clientId}")
    @Transactional
    public ResponseEntity<?> deleteClient(@PathVariable("clientId") String clientId) {
        Optional<Client> client = cr.findById(clientId);
        if (client.isPresent()) {
            cr.delete(client.get());
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{clientId}")
    @Transactional
    public ResponseEntity<?> updateClient(@RequestBody Client client,
                                               @PathVariable("clientId") String clientId) {
        Optional<Client> body = Optional.ofNullable(client);
        if (!body.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        if (!cr.existsById(clientId)) {
            return ResponseEntity.notFound().build();
        }
        client.setIdclient(clientId);
        cr.save(client);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{clientId}")
    @Transactional
    public ResponseEntity<?> updateClientPartiel(@PathVariable("clientId") String clientId,
                                                      @RequestBody Map<Object, Object> fields) {
        Optional<Client> body = cr.findById(clientId);
        if (body.isPresent()) {
            Client client = body.get();
            fields.forEach((f, v) -> {
                Field field = ReflectionUtils.findField(Client.class, f.toString());
                field.setAccessible(true);
                ReflectionUtils.setField(field, client, v);
            });
            cv.validate(new ClientInput(client.getNom(),
                    client.getPrenom(),
                    client.getSecret(),
                    client.getDatenaiss(),
                    client.getPays(),
                    client.getNopasseport(),
                    client.getNumtel(),
                    client.getComptes()));
            client.setIdclient(clientId);
            cr.save(client);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
