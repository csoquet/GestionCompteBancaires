package org.miage.Banque.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.miage.Banque.entity.Client;
import org.miage.Banque.entity.Role;
import org.miage.Banque.resource.ClientResource;
import org.miage.Banque.resource.RoleResource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ClientService implements UserDetailsService {

    private final RoleResource rr;
    private final ClientResource cr;

    private final PasswordEncoder passwordEncoder;

    public Iterable<Client> findAll(){ return cr.findAll();}

    public Client findById(String clientId){
        return cr.getByIdclient(clientId);
    }

    public boolean existById(String clientId) {return cr.existsByIdclient(clientId);}

    public Client saveClient(Client client){
        log.info("Sauvegarde de nouveau client");
        client.setSecret(passwordEncoder.encode(client.getSecret()));
        cr.save(client);
        client = addRoleToClient(client.getEmail(), "ROLE_USER");
        return client;
    }

    public void delete(Client client) {cr.delete(client);}

    public Role saveRole(Role role){
        log.info("Sauvegarde de nouveau role");
        return rr.save(role);
    }
    public Client addRoleToClient(String email, String roleNom){
        log.info("Ajout du role au client");
        Client client = cr.findByEmail(email);
        Role role = rr.findByNom(roleNom);
        client.getRoles().add(role);
        return client;

    }

    public void deleteAll(){
        cr.deleteAll();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Client client = cr.findByEmail(email);
        if(client ==  null){
            log.error("Client non trouvé");
            throw  new UsernameNotFoundException("Client non trouvé");
        }
        else{
            log.info("Client trouvé");
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        client.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getNom()));
        });
        return new org.springframework.security.core.userdetails.User(client.getEmail(), client.getSecret(), authorities);
    }


}
