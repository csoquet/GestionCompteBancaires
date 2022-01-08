package org.miage.Banque.representation;

import org.miage.Banque.assembler.ClientAssembler;
import org.miage.Banque.entity.Client;
import org.miage.Banque.input.ClientInput;
import org.miage.Banque.resource.ClientResource;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value="/clients", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Client.class)
public class ClientRepresentation {

    private final ClientResource cr;
    private final ClientAssembler ca;

    public ClientRepresentation(ClientResource cr, ClientAssembler ca) {
        this.cr = cr;
        this.ca = ca;
    }

    @GetMapping
    public ResponseEntity<?> getAllClients() {
        Iterable<Client> clients = cr.findAll();
        return ResponseEntity.ok(ca.toCollectionModel(clients));
    }

    @GetMapping(value = "/{clientId}")
    public ResponseEntity<?> getOneClient(@PathVariable("clientId") String id) {
        return Optional.of(cr.findById(id)).filter(Optional::isPresent)
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
                client.getNumtel()
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

}
