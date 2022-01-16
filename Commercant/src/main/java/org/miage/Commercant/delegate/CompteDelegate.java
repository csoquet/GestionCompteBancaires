package org.miage.Commercant.delegate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class CompteDelegate {

    @Lazy
    @Autowired
    RestTemplate restTemplate;

    public String callTest(String clientId){
        System.out.println("test : "+ clientId);
        String response = restTemplate.exchange("http://banque-service/clients/{clientId}/comptes", HttpMethod.GET, null, new ParameterizedTypeReference<String>(){}, clientId).getBody();
        System.out.println("Response Received as " + response + " -  " + new Date());
        return response;
    }


    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
