package org.miage.Banque.representation;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.miage.Banque.assembler.ClientAssembler;
import org.miage.Banque.entity.Client;
import org.miage.Banque.entity.Role;
import org.miage.Banque.input.ClientInput;
import org.miage.Banque.service.ClientService;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value="/clients", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Client.class)
@RequiredArgsConstructor
@Slf4j
public class ClientRepresentation {

    private final ClientAssembler ca;
    private final ClientService clientService;



    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllClients() {
        Iterable<Client> clients = clientService.findAll();
        return ResponseEntity.ok(ca.toCollectionModel(clients));
    }

    @GetMapping(value = "/{clientId}")
    @PostAuthorize("returnObject.content.email == authentication.name or hasRole('ROLE_ADMIN')")
    public EntityModel getOneClient(@PathVariable("clientId") String id) {
            return ca.toModel(clientService.findById(id));

    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveClient(@RequestBody @Valid ClientInput client) {
        Client clientSave = new Client(
                UUID.randomUUID().toString(),
                client.getNom(),
                client.getPrenom(),
                client.getEmail(),
                client.getSecret(),
                client.getDatenaiss(),
                client.getPays(),
                client.getNopasseport(),
                client.getNumtel(),
                new ArrayList<>()
        );
        Client saved = clientService.saveClient(clientSave);
        URI location = linkTo(ClientRepresentation.class).slash(saved.getIdclient()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/role")
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> saveRole(@RequestBody @Valid Role role) {
        Role roleSave = new Role(
                UUID.randomUUID().toString(),
                role.getNom()
        );
        Role saved = clientService.saveRole(roleSave);
        URI location = linkTo(ClientRepresentation.class).slash(saved.getId()).toUri();
        return ResponseEntity.created(null).build();
    }

    @PostMapping("/role/addtoclient")
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> saveRoleToClient(@RequestBody RoleToClient form) {
        clientService.addRoleToClient(form.getEmail(), form.getRoleNom());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{clientId}")
    @Transactional
    public ResponseEntity<?> deleteClient(@PathVariable("clientId") String clientId, @AuthenticationPrincipal String clientEmail) {
        if(clientService.existById(clientId)){
            Client client = clientService.findById(clientId);
            if(client.getEmail().equals(clientEmail)){
                clientService.delete(client);
                return ResponseEntity.noContent().build();
            }
            throw new RuntimeException("Impossible de supprimer un autre client");
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{clientId}")
    @Transactional
    public ResponseEntity<?> updateClient(@RequestBody Client client,
                                          @PathVariable("clientId") String clientId,
                                          @AuthenticationPrincipal String clientEmail) {
        Client body = client;
        if (body.getEmail().equals(clientEmail)){
            if (!clientService.existById(clientId)) {
                return ResponseEntity.notFound().build();
            }
            client.setIdclient(clientId);
            clientService.saveClient(client);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}

@Data
class RoleToClient{
    private String email;
    private String roleNom;
}
