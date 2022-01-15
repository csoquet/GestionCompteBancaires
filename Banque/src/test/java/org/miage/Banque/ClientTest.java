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
import org.miage.Banque.entity.Client;
import org.miage.Banque.entity.Role;
import org.miage.Banque.input.ClientInput;
import org.miage.Banque.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientTest {

    @LocalServerPort
    int port;


    @Autowired
    ClientService clientService;

    @BeforeEach
    public void setupContext() {
        clientService.deleteAll();
        RestAssured.port = port;
    }


    @Test
    public void getOneClient() throws Exception {
        Role role = new Role("1", "ROLE_USER");
        Role role2 = new Role("2", "ROLE_ADMIN");

        clientService.saveRole(role);
        clientService.saveRole(role2);


        String secret = "0000";
        Client client1 = new Client(UUID.randomUUID().toString(),"Picard","Jeremy", "jeremy@test.fr",secret, "08-06-1995", "France", "30RF07125", "0625126321", new ArrayList<>());
        client1 = clientService.saveClient(client1);

        client1 = clientService.addRoleToClient(client1.getEmail(), "ROLE_USER");

        String access_token = getToken(client1.getEmail(), secret);
        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when().get("/clients/" + client1.getIdclient())
                .then()
                .extract().response();

        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString("Jeremy"));
    }

    @Test
    public void getAllClientUser() throws Exception { // Un client n'a pas le droit de visualiser tous les clients
        Role role = new Role("1", "ROLE_USER");
        Role role2 = new Role("2", "ROLE_ADMIN");

        clientService.saveRole(role);
        clientService.saveRole(role2);


        String secret = "0000";
        Client client1 = new Client("3","Picard","Jeremy", "jeremy@test.fr",secret, "08-06-1995", "France", "30RF07125", "0625126321", new ArrayList<>());
        clientService.saveClient(client1);

        clientService.addRoleToClient(client1.getEmail(), "ROLE_USER");

        String access_token = getToken(client1.getEmail(), secret);
        given()
                .header("Authorization", "Bearer " + access_token)
                .when().get("/clients")
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void getAllClientAdmin() throws Exception { //L'admin a le droit de visualiser tous les clients
        Role role = new Role("1", "ROLE_USER");
        Role role2 = new Role("2", "ROLE_ADMIN");

        clientService.saveRole(role);
        clientService.saveRole(role2);

        String secret = "0000";
        Client client1 = new Client(UUID.randomUUID().toString(),"Picard","Jeremy", "jeremy@test.fr",secret, "08-06-1995", "France", "30RF07125", "0625126321", new ArrayList<>());
        clientService.saveClient(client1);
        Client client2 = new Client(UUID.randomUUID().toString(),"Picardo","Jeremyo", "jeremyo@test.fr",secret, "12-06-1995", "France", "85RF01225", "0617126321", new ArrayList<>());
        clientService.saveClient(client2);


        clientService.addRoleToClient(client1.getEmail(), "ROLE_ADMIN");

        String access_token = getToken(client1.getEmail(), secret);
        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when().get("/clients")
                .then()
                .extract().response();

        String json = response.asString();
        int nb = StringUtils.countOccurrencesOf(json, "email"); //Compte le nombre de client grâce a son email
        assertThat(nb, equalTo(2));
    }

    @Test
    public void postClient() throws ParseException, IOException {
        String secret = "0000";
        ClientInput client = new ClientInput("Picard","Jeremy", "jeremy@test.fr", secret, "08-06-1995", "France", "30RF07125", "0624512314");

       given()
                .body(this.toJsonString(client))
                .contentType(ContentType.JSON)
                .when()
                .post("/clients")
                .then()
                .statusCode(HttpStatus.SC_CREATED);
    }

    @Test
    public void putClient() throws ParseException, IOException, JSONException{
        Role role = new Role("1", "ROLE_USER");
        Role role2 = new Role("2", "ROLE_ADMIN");

        clientService.saveRole(role);
        clientService.saveRole(role2);

        String secret = "0000";
        Client client1 = new Client("6","Picarda","Jeremya", "jeremya@test.fr",secret, "08-06-1995", "France", "30RF07125", "0625126321", new ArrayList<>());
        client1 = clientService.saveClient(client1);

        String secret2="0000";
        ClientInput clientInput = new ClientInput("Picardo","Jeremy", "jeremya@test.fr",secret2, "08-06-1995", "France", "30RF07125", "0625126321");

        String access_token = getToken(client1.getEmail(), secret);

        given()
                .header("Authorization", "Bearer " + access_token)
                .body(this.toJsonString(clientInput))
                .contentType(ContentType.JSON)
                .when()
                .put("/clients/" + client1.getIdclient())
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void deleteClient() throws Exception {
        Role role = new Role("1", "ROLE_USER");
        Role role2 = new Role("2", "ROLE_ADMIN");

        clientService.saveRole(role);
        clientService.saveRole(role2);


        String secret = "0000";
        Client client1 = new Client("3","Picard","Jeremy", "jeremy@test.fr",secret, "08-06-1995", "France", "30RF07125", "0625126321", new ArrayList<>());
        clientService.saveClient(client1);

        clientService.addRoleToClient(client1.getEmail(), "ROLE_USER");
        String access_token = getToken(client1.getEmail(), secret);

        given()
                .header("Authorization", "Bearer " + access_token)
                .when().delete("/clients/" + client1.getIdclient())
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void postRoleAdmin() throws Exception {
        Role role = new Role("1", "ROLE_USER");
        Role role2 = new Role("2", "ROLE_ADMIN");

        clientService.saveRole(role);
        clientService.saveRole(role2);


        String secret = "0000";
        Client client1 = new Client("3","Picard","Jeremy", "jeremy@test.fr",secret, "08-06-1995", "France", "30RF07125", "0625126321", new ArrayList<>());
        client1 = clientService.saveClient(client1);

        client1= clientService.addRoleToClient(client1.getEmail(), "ROLE_ADMIN");
        String access_token = getToken(client1.getEmail(), secret);

        Role roleTest = new Role("3", "ROLE_TEST");

        given()
                .header("Authorization", "Bearer " + access_token)
                .body(this.toJsonString(roleTest))
                .contentType(ContentType.JSON)
                .when()
                .post("/clients/role")
                .then()
                .statusCode(HttpStatus.SC_CREATED);
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
