package org.miage.Banque;

import static io.restassured.RestAssured.when;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miage.Banque.entity.Client;
import org.miage.Banque.entity.Compte;
import org.miage.Banque.input.CompteInput;
import org.miage.Banque.resource.CompteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.apache.http.HttpStatus;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CompteTests {

    /*@LocalServerPort
    int port;

    @Autowired
    CompteResource cr;

    @BeforeEach
    public void setupContext() {
        cr.deleteAll();
        RestAssured.port = port;
    }

    @Test
    public void pingApi() {
        when().get("/comptes").then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void getOne() {
        Compte i1 = new Compte(UUID.randomUUID().toString(),"202012",20.0, null, "");
        cr.save(i1);
        Response response = when().get("/comptes/"+i1.getIdcompte())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString,containsString("202012"));
    }

    @Test
    public void getAll() {
        Compte i1 = new Compte(UUID.randomUUID().toString(),"202012",20.0, null, "");
        cr.save(i1);
        Compte i2 = new Compte(UUID.randomUUID().toString(),"205042",20.0, null, "");
        cr.save(i2);
        when().get("/comptes/")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .assertThat()
                .body("size()",equalTo(2));
    }

    @Test
    public void getNotFound() {
        when().get("/comptes/99999").then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    private String toJsonString(Object o) throws Exception {
        ObjectMapper map = new ObjectMapper();
        return map.writeValueAsString(o);
    }

    @Test
    public void postApi() throws Exception{
        CompteInput i1 = new CompteInput("202012",20.0, null);
        Response response = given()
                .body(this.toJsonString(i1))
                .contentType(ContentType.JSON)
                .when()
                .post("/comptes")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .response();
        String location = response.getHeader("Location");
        when().get(location).then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void deleteApi() throws Exception {
        Compte i1 = new Compte(UUID.randomUUID().toString(),"202012",20.0, null, "");
        cr.save(i1);
        when().delete("/comptes/" + i1.getIdcompte()).then().statusCode(HttpStatus.SC_NO_CONTENT);
        when().get("/comptes/" + i1.getIdcompte()).then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void deleteWithClient() throws Exception {
        Client test = new Client(UUID.randomUUID().toString(), "test", "test", "secret", "28-09-1997","France","22222","0624568498");
        Compte i1 = new Compte(UUID.randomUUID().toString(),"202012",20.0, null, "");
        cr.save(i1);
        when().delete("/comptes/" + i1.getIdcompte()).then().statusCode(HttpStatus.SC_NO_CONTENT);
        when().get("/comptes/" + i1.getIdcompte()).then().statusCode(HttpStatus.SC_NOT_FOUND);
    }*/

}
