package org.miage.Commercant.config;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CommercantConfig {

    @Bean
    @Primary
    ServiceInstanceListSupplier service(){
        return new DemoServiceInstanceListSuppler("commercant");
    }
}

class DemoServiceInstanceListSuppler implements ServiceInstanceListSupplier {
    private final String serviceId;

    public DemoServiceInstanceListSuppler(String idService) {
        this.serviceId = idService;
    }

    @Override
    public String getServiceId(){
        return serviceId;
    }

    @Override
    public Flux<List<ServiceInstance>> get(){
        return Flux.just(Arrays
                .asList(new DefaultServiceInstance(serviceId + "1", serviceId,
                                "localhost", 8082, false),
                        new DefaultServiceInstance(serviceId + "2", serviceId, "localhost",
                                8083, false),
                        new DefaultServiceInstance(serviceId + "3", serviceId, "localhost",
                                8084, false)));
    }
}
