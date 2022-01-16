package org.miage.Commercant.delegate;


import net.minidev.json.JSONObject;
import org.miage.Commercant.entity.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OperationDelegate {


    @Lazy
    @Autowired
    RestTemplate restTemplate;


    public String callPostOperation(String clientId, String compteId, Operation operation){

        HttpHeaders entete = new HttpHeaders();
        entete.setContentType(MediaType.APPLICATION_JSON);
        JSONObject objectJson = new JSONObject();
        objectJson.put("libelle", operation.getLibelle());
        objectJson.put("montant", operation.getMontant());
        objectJson.put("tauxapplique", operation.getTauxapplique());
        objectJson.put("categorie", operation.getCategorie());
        objectJson.put("pays", operation.getPays());
        objectJson.put("comptecrediteurIban", operation.getComptecrediteurIban());
        objectJson.put("carteNumero", operation.getCarteNumero());
        objectJson.put("codeCarte", operation.getCodeCarte());

        HttpEntity<String> request = new HttpEntity<String>(objectJson.toString(), entete);
        String response = restTemplate.postForObject("http://banque-service/clients/{clientId}/comptes/{compteId}/operations", request, String.class, clientId, compteId);
        System.out.println("Ajout de l'opération");
        return response;
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
