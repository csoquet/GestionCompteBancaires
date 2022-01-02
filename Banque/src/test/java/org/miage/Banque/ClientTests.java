package org.miage.Banque;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miage.Banque.entity.Client;
import org.miage.Banque.input.ClientInput;
import org.miage.Banque.resource.ClientResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientTests {

   /* @LocalServerPort
    int port;

    @Autowired
    ClientResource cr;

    @BeforeEach
    public void setupContext() {
        cr.deleteAll();
        RestAssured.port = port;
    }

    @Test
    public void pingApi() {
        when().get("/clients").then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void getOne() {
        Client i2 = new Client(UUID.randomUUID().toString(), "Picard","Remy", "0000", "15-02-1992", "France", "0101020203", "0625123621");
        cr.save(i2);
        when().get("/clients/" +i2.getIdclient())
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void getAll() {
        Client i1 = new Client(UUID.randomUUID().toString(), "Dupont","Bruno", "0000", "15-02-1992", "France", "0101020203", "0625123621");
        cr.save(i1);
        Client i2 = new Client(UUID.randomUUID().toString(), "Picard","Remy", "0000", "15-02-1992", "France", "0101020203", "0625123621");
        cr.save(i2);
        when().get("/clients/")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .assertThat()
                .body("size()",equalTo(2));
    }

    @Test
    public void getNotFound() {
        when().get("/clients/99999").then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    private String toJsonString(Object o) throws Exception {
        ObjectMapper map = new ObjectMapper();
        return map.writeValueAsString(o);
    }

    @Test
    public void postApi() throws Exception{
        ClientInput i1 = new ClientInput("Dupont","Bruno", "0000", "15-02-1992", "France", "0101020203", "0625123621");
        Response response = given()
                .body(this.toJsonString(i1))
                .contentType(ContentType.JSON)
                .when()
                .post("/clients")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .response();
        String location = response.getHeader("Location");
        when().get(location).then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void deleteApi() throws Exception {
        Client i1 = new Client(UUID.randomUUID().toString(), "Dupont","Bruno", "0000", "15-02-1992", "France", "0101020203", "0625123621");
        cr.save(i1);
        when().delete("/clients/" + i1.getIdclient()).then().statusCode(HttpStatus.SC_NO_CONTENT);
        when().get("/clients/" + i1.getIdclient()).then().statusCode(HttpStatus.SC_NOT_FOUND);
    }*/


}
