package org.miage.Banque.representation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.miage.Banque.assembler.ClientAssembler;
import org.miage.Banque.entity.Client;
import org.miage.Banque.entity.Role;
import org.miage.Banque.input.ClientInput;
import org.miage.Banque.service.ClientService;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value="/clients")
@ExposesResourceFor(Client.class)
@RequiredArgsConstructor
@Slf4j
public class ClientRepresentation {

    private final ClientAssembler ca;
    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<?> getAllClients() {
        Iterable<Client> clients = clientService.findAll();
        return ResponseEntity.ok(ca.toCollectionModel(clients));
    }

    @GetMapping(value = "/{clientId}")
    public ResponseEntity<?> getOneClient(@PathVariable("clientId") String id) {
        return Optional.of(clientService.findById(id)).filter(Optional::isPresent)
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
    public ResponseEntity<?> saveRoleToClient(@RequestBody RoleToClient form) {
        clientService.addRoleToClient(form.getEmail(), form.getRoleNom());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            try{
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String email = decodedJWT.getSubject();
                Client user = clientService.getClient(email);
                String accessToken = JWT.create()
                        .withSubject(user.getEmail())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles().stream().map(Role::getNom).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch(Exception exception){
                response.setHeader("error", exception.getMessage());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                //response.sendError(HttpStatus.FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }

        }
        else{
            throw new RuntimeException("Refresh token manquant");
        }
    }



    @DeleteMapping(value = "/{clientId}")
    @Transactional
    public ResponseEntity<?> deleteClient(@PathVariable("clientId") String clientId) {
        Optional<Client> client = clientService.findById(clientId);
        if (client.isPresent()) {
            clientService.delete(client.get());
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
        if (!clientService.existById(clientId)) {
            return ResponseEntity.notFound().build();
        }
        client.setIdclient(clientId);
        clientService.saveClient(client);
        return ResponseEntity.ok().build();
    }

}

@Data
class RoleToClient{
    private String email;
    private String roleNom;
}
