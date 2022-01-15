package org.miage.Banque;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miage.Banque.entity.*;
import org.miage.Banque.input.OperationInput;
import org.miage.Banque.resource.CarteBancaireResource;
import org.miage.Banque.resource.CompteResource;
import org.miage.Banque.resource.OperationResource;
import org.miage.Banque.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static java.lang.Boolean.FALSE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OperationTest {

    @LocalServerPort
    int port;


    @Autowired
    CompteResource compteResource;

    @Autowired
    ClientService clientService;

    @Autowired
    CarteBancaireResource carteBancaireResource;

    @Autowired
    OperationResource operationResource;

    @BeforeEach
    public void setupContext() {
        compteResource.deleteAll();
        clientService.deleteAll();
        carteBancaireResource.deleteAll();
        operationResource.deleteAll();
        RestAssured.port = port;
    }

    @Test
    public void getAllOperationsByIdCompte() throws Exception {
        Role role = new Role("1", "ROLE_USER");
        clientService.saveRole(role);

        String secret = "0000";
        Client client1 = new Client("3","Picard","Jeremy", "jeremy@test.fr",secret, "08-06-1995", "France", "30RF07125", "0625126321", new ArrayList<>());
        client1 = clientService.saveClient(client1);

        Compte compte1 = new Compte("FR7612548029989876587210917", 500.0, client1);
        compteResource.save(compte1);

        Compte compte2 = new Compte("FR7612548029989876587219817", 300.0, client1);
        compteResource.save(compte2);

        CarteBancaire carte1 = new CarteBancaire("1234567891234567", "1234", "123", FALSE, FALSE, 1000.0, FALSE, FALSE,"15-03-2022", FALSE, compte1);
        carteBancaireResource.save(carte1);

        Operation operation1 = new Operation("1",new Date(2022-01-01),"Courses", 20.0, 1.0, "Magasin", "France", compte1,compte2, carte1);
        operationResource.save(operation1);

        String access_token = getToken(client1.getEmail(), secret);

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when().get("/clients/"+client1.getIdclient()+"/comptes/"+compte1.getIban()+"/operations")
                .then()
                .extract().response();

        String json = response.asString();
        int nb = StringUtils.countOccurrencesOf(json, "libelle"); //Compte le nombre d'operation grâce au libelle
        assertThat(nb, equalTo(1));
    }

    @Test
    public void getOneOperationByIdCompte() throws Exception {
        Role role = new Role("1", "ROLE_USER");
        clientService.saveRole(role);

        String secret = "0000";
        Client client1 = new Client("3","Picard","Jeremy", "jeremy@test.fr",secret, "08-06-1995", "France", "30RF07125", "0625126321", new ArrayList<>());
        client1 = clientService.saveClient(client1);

        Compte compte1 = new Compte("FR7612548029989876587210917", 500.0, client1);
        compteResource.save(compte1);

        Compte compte2 = new Compte("FR7612548029989876587219817", 300.0, client1);
        compteResource.save(compte2);

        CarteBancaire carte1 = new CarteBancaire("1234567891234567", "1234", "123", FALSE, FALSE, 1000.0, FALSE, FALSE,"15-03-2022", FALSE, compte1);
        carteBancaireResource.save(carte1);

        Operation operation1 = new Operation("5",new Date(2022-01-01),"Courses", 20.0, 1.0, "Magasin", "France", compte1,compte2, carte1);
        operation1 = operationResource.save(operation1);

        String access_token = getToken(client1.getEmail(), secret);

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when().get("/clients/"+client1.getIdclient()+"/comptes/"+compte1.getIban()+"/operations/"+operation1.getIdoperation())
                .then()
                .extract().response();

        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString("Magasin"));
    }

    @Test
    public void postOperation() throws ParseException, IOException, JSONException {
        Role role = new Role("1", "ROLE_USER");
        clientService.saveRole(role);

        String secret = "0000";
        Client client1 = new Client("3","Picard","Jeremy", "jeremy@test.fr",secret, "08-06-1995", "France", "30RF07125", "0625126321", new ArrayList<>());
        client1 = clientService.saveClient(client1);

        Double solde1 = 500.0;
        Compte compte1 = new Compte("FR7612548029989876587210917", solde1, client1);
        compte1 = compteResource.save(compte1);

        Double solde2 = 300.0;
        Compte compte2 = new Compte("FR7612548029989876587219817", solde2, client1);
        compte2 = compteResource.save(compte2);

        CarteBancaire carte1 = new CarteBancaire("1234567891234567", "1234", "123", FALSE, FALSE, 1000.0, FALSE, FALSE,"15-03-2022", FALSE, compte1);
        carte1 = carteBancaireResource.save(carte1);

        OperationInput operationInput = new OperationInput("Coupe de cheveux", 20.0, 1.0, "Coiffeur", "France", compte2.getIban(), carte1.getNumcarte(), carte1.getCode());

        String access_token = getToken(client1.getEmail(), secret);

        given()
                .header("Authorization", "Bearer " + access_token)
                .body(this.toJsonString(operationInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/clients/" + client1.getIdclient() + "/comptes/"+compte1.getIban()+"/operations")
                .then()
                .statusCode(HttpStatus.SC_CREATED);

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .get("/clients/"+client1.getIdclient()+"/comptes/"+compte1.getIban())
                .then()
                .extract().response();

        String json = response.asString();
        assertThat(json, containsString(String.valueOf(solde1-operationInput.getMontant())));

        Response response2 = given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .get("/clients/"+client1.getIdclient()+"/comptes/"+compte2.getIban())
                .then()
                .extract().response();

        String json2 = response2.asString();
        assertThat(json2, containsString(String.valueOf(solde2+operationInput.getMontant())));


    }

    private String toJsonString(Object o) throws JsonProcessingException {
        ObjectMapper map = new ObjectMapper();
        return map.writeValueAsString(o);
    }

    private String getToken(String email, String password) throws JSONException { //Permet de récupérer le token d'une personne existante

        String jsonBody = String.format("email=%s&password=%s", email, password); //On définit le format du body

        Response response = given()
                .with()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(jsonBody)
                .when()
                .post("/login")//Va appeler la route du login pour récupérer le token
                .then()
                .extract()
                .response();

        String responseString = response.asString();
        JSONObject jsonO = new JSONObject(responseString);
        return jsonO.getString("access_token"); //On renvoit seulement le access_token
    }
}
