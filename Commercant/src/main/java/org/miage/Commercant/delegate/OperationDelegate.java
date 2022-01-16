package org.miage.Commercant.delegate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class OperationDelegate {


    @Lazy
    @Autowired
    RestTemplate restTemplate;

    /*public String callPostOperation(String clientId, String compteId, Operation operation){

        HttpHeaders entete = new HttpHeaders();
        entete.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("libelle", operation.getLibelle());
        map.add("montant", String.valueOf(operation.getMontant()));
        map.add("tauxapplique", String.valueOf(operation.getTauxapplique()));
        map.add("categorie", operation.getCategorie());
        map.add("pays", operation.getPays());
        map.add("comptecrediteurIban", operation.getComptecrediteurIban());
        map.add("carteNumero", operation.getCarteNumero());
        map.add("codeCarte", operation.getCodeCarte());

        HttpEntity<MultiValueMap<String, String>> requeteHttp = new HttpEntity<MultiValueMap<String, String>>(map, entete);

        String response = restTemplate.exchange("http://banque-service/clients/{clientId}/comptes/{compteId}/operations", HttpMethod.POST, requeteHttp, new ParameterizedTypeReference<String>(){}, clientId, compteId).getBody();
        System.out.println("Response Received as " + response + " -  " + new Date());
        return response;
    }*/
}
