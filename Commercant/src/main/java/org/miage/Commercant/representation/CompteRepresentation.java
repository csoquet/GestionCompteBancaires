package org.miage.Commercant.representation;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(value="/clients/{clientId}/comptes", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompteRepresentation {

    private final ReactorLoadBalancerExchangeFilterFunction reactor;
    private final WebClient.Builder builder;

    public CompteRepresentation(ReactorLoadBalancerExchangeFilterFunction reactor, WebClient.Builder builder){
        this.reactor = reactor;
        this.builder = builder;
    }

    @GetMapping
    public Mono<Object> test (@PathVariable(value = "clientId") String clientId){
        return builder.build().get().uri("http://localhost:8082/clients/" + clientId + "/comptes")
                .retrieve().bodyToMono(Object.class);
    }

}
