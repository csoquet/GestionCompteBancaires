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
import org.miage.Banque.entity.CarteBancaire;
import org.miage.Banque.entity.Client;
import org.miage.Banque.entity.Compte;
import org.miage.Banque.entity.Role;
import org.miage.Banque.input.CarteBancaireInput;
import org.miage.Banque.resource.CarteBancaireResource;
import org.miage.Banque.resource.CompteResource;
import org.miage.Banque.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarteBancaireTest {

    @LocalServerPort
    int port;


    @Autowired
    CompteResource compteResource;

    @Autowired
    ClientService clientService;

    @Autowired
    CarteBancaireResource carteBancaireResource;

    @BeforeEach
    public void setupContext() {
        carteBancaireResource.deleteAll();
        RestAssured.port = port;
    }

    @Test
    public void getAllCarteByIdCompte() throws Exception { // Un client n'a pas le droit de visualiser tous les clients
        Role role = new Role("1", "ROLE_USER");
        clientService.saveRole(role);

        String secret = "0000";
        Client client1 = new Client("3","Picard","Jeremy", "jeremy@test.fr",secret, "08-06-1995", "France", "30RF07125", "0625126321", new ArrayList<>());
        client1 = clientService.saveClient(client1);

        Compte compte1 = new Compte("FR7612548029989876587210917", 500.0, client1);
        compteResource.save(compte1);

        CarteBancaire carte1 = new CarteBancaire("1234567891234567", "1234", "123", FALSE, FALSE, 1000.0, TRUE, FALSE,"15-03-2022", FALSE, compte1);
        carteBancaireResource.save(carte1);

        CarteBancaire carte2 = new CarteBancaire("1234567891235457", "4321", "321", TRUE, FALSE, 2000.0, FALSE, FALSE,"10-05-2023",FALSE, compte1);
        carteBancaireResource.save(carte2);

        String access_token = getToken(client1.getEmail(), secret);

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when().get("/clients/"+client1.getIdclient()+"/comptes/"+compte1.getIban()+"/cartebancaires")
                .then()
                .extract().response();

        String json = response.asString();
        int nb = StringUtils.countOccurrencesOf(json, "code"); //Compte le nombre de carte bancaire grâce au code
        assertThat(nb, equalTo(2));
    }

    @Test
    public void getOneCarteByIdCompte() throws Exception {
        Role role = new Role("1", "ROLE_USER");
        clientService.saveRole(role);

        String secret = "0000";
        Client client1 = new Client(UUID.randomUUID().toString(),"Picard","Jeremy", "jeremy@test.fr",secret, "08-06-1995", "France", "30RF07125", "0625126321", new ArrayList<>());
        client1 = clientService.saveClient(client1);

        Compte compte1 = new Compte("FR7612548029989876587210917", 500.0, client1);
        compteResource.save(compte1);

        CarteBancaire carte1 = new CarteBancaire("1234567891234567", "1234", "123", FALSE, FALSE, 1000.0, TRUE, FALSE,"15-03-2022", FALSE, compte1);
        carteBancaireResource.save(carte1);

        String access_token = getToken(client1.getEmail(), secret);

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when().get("/clients/" + client1.getIdclient() + "/comptes/" + compte1.getIban() + "/cartebancaires/"+carte1.getNumcarte())
                .then()
                .extract().response();

        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString("1234"));
    }

    @Test
    public void postCarteBancaire() throws ParseException, IOException, JSONException {
        Role role = new Role("1", "ROLE_USER");
        clientService.saveRole(role);

        String secret = "0000";
        Client client1 = new Client(UUID.randomUUID().toString(),"Picard","Jeremy", "jeremy@test.fr",secret, "08-06-1995", "France", "30RF07125", "0625126321", new ArrayList<>());
        client1 = clientService.saveClient(client1);

        Compte compte1 = new Compte("FR7612548029989876587210917", 500.0, client1);
        compteResource.save(compte1);

        CarteBancaireInput carteBancaireInput = new CarteBancaireInput(TRUE, 100.0, TRUE, FALSE);
        String access_token = getToken(client1.getEmail(), secret);

        given()
                .header("Authorization", "Bearer " + access_token)
                .body(this.toJsonString(carteBancaireInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/clients/" + client1.getIdclient() + "/comptes/"+compte1.getIban()+"/cartebancaires")
                .then()
                .statusCode(HttpStatus.SC_CREATED);
    }

    @Test
    public void postCarteBancaireVirtuelle() throws ParseException, IOException, JSONException {
        Role role = new Role("1", "ROLE_USER");
        clientService.saveRole(role);

        String secret = "0000";
        Client client1 = new Client(UUID.randomUUID().toString(),"Picard","Jeremy", "jeremy@test.fr",secret, "08-06-1995", "France", "30RF07125", "0625126321", new ArrayList<>());
        client1 = clientService.saveClient(client1);

        Compte compte1 = new Compte("FR7612548029989876587210917", 500.0, client1);
        compteResource.save(compte1);

        CarteBancaireInput carteBancaireInput = new CarteBancaireInput(TRUE, 100.0, TRUE, TRUE);
        String access_token = getToken(client1.getEmail(), secret);

        given()
                .header("Authorization", "Bearer " + access_token)
                .body(this.toJsonString(carteBancaireInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/clients/" + client1.getIdclient() + "/comptes/"+compte1.getIban()+"/cartebancaires")
                .then()
                .statusCode(HttpStatus.SC_CREATED);
    }

    @Test
    public void deleteCompte() throws Exception {
        Role role = new Role("1", "ROLE_USER");
        clientService.saveRole(role);

        String secret = "0000";
        Client client1 = new Client(UUID.randomUUID().toString(),"Picard","Jeremy", "jeremy@test.fr",secret, "08-06-1995", "France", "30RF07125", "0625126321", new ArrayList<>());
        client1 = clientService.saveClient(client1);

        Compte compte1 = new Compte("FR7612548029989876587210917", 500.0, client1);
        compteResource.save(compte1);

        CarteBancaire carte1 = new CarteBancaire("1234567891234567", "1234", "123", FALSE, FALSE, 1000.0, TRUE, FALSE,"15-03-2022", FALSE, compte1);
        carteBancaireResource.save(carte1);

        String access_token = getToken(client1.getEmail(), secret);

        given()
                .header("Authorization", "Bearer " + access_token)
                .when().delete("/clients/" + client1.getIdclient() + "/comptes/" + compte1.getIban()+"/cartebancaires/"+carte1.getNumcarte())
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void putCarteBancaire() throws ParseException, IOException, JSONException{
        Role role = new Role("1", "ROLE_USER");
        clientService.saveRole(role);

        String secret = "0000";
        Client client1 = new Client("6","Picarda","Jeremya", "jeremya@test.fr",secret, "08-06-1995", "France", "30RF07125", "0625126321", new ArrayList<>());
        client1 = clientService.saveClient(client1);

        Compte compte1 = new Compte("FR7612548029989876587210917", 500.0, client1);
        compteResource.save(compte1);

        CarteBancaire carte1 = new CarteBancaire("1234567891234567", "1234", "123", FALSE, FALSE, 1000.0, TRUE, FALSE,"15-03-2022", FALSE, compte1);
        carteBancaireResource.save(carte1);

        CarteBancaireInput carteBancaireInput = new CarteBancaireInput(FALSE, 1100.0, TRUE, FALSE);

        String access_token = getToken(client1.getEmail(), secret);

        given()
                .header("Authorization", "Bearer " + access_token)
                .body(this.toJsonString(carteBancaireInput))
                .contentType(ContentType.JSON)
                .when()
                .put("/clients/" + client1.getIdclient()+"/comptes/"+compte1.getIban()+"/cartebancaires/"+carte1.getNumcarte())
                .then()
                .statusCode(HttpStatus.SC_OK);
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
